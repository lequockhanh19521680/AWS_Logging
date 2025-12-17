package com.fintech.wallet.service;

import com.fintech.common.event.TransactionCreatedEvent;
import com.fintech.common.event.WalletProcessedEvent;
import com.fintech.wallet.dto.TransactionRequest;
import com.fintech.wallet.model.LedgerEntry;
import com.fintech.wallet.model.LedgerEntryType;
import com.fintech.wallet.model.Wallet;
import com.fintech.wallet.model.WalletStatus;
import com.fintech.wallet.repository.LedgerEntryRepository;
import com.fintech.wallet.repository.WalletRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Long SYSTEM_USER_ID = 0L;

    @PostConstruct
    public void init() {
        if (walletRepository.findByUserId(SYSTEM_USER_ID).isEmpty()) {
            createWallet(SYSTEM_USER_ID);
            log.info("System wallet created for User ID 0");
        }
    }

    public Wallet createWallet(Long userId) {
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Wallet already exists for user: " + userId);
        }
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .currency("USD")
                .status(WalletStatus.ACTIVE)
                .build();
        return walletRepository.save(wallet);
    }

    public Wallet getWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
    }

    public List<LedgerEntry> getLedgerHistory(Long userId) {
        Wallet wallet = getWallet(userId);
        return ledgerEntryRepository.findByWalletId(wallet.getId());
    }

    @KafkaListener(topics = "transaction-events", groupId = "wallet-group")
    @Transactional
    public void handleTransactionEvent(TransactionCreatedEvent event) {
        log.info("Received transaction event: {}", event);
        try {
            processWalletOperation(event);
            kafkaTemplate.send("wallet-events", WalletProcessedEvent.builder()
                    .transactionId(event.getTransactionId())
                    .status("SUCCESS")
                    .build());
        } catch (Exception e) {
            log.error("Failed to process transaction: {}", e.getMessage());
            kafkaTemplate.send("wallet-events", WalletProcessedEvent.builder()
                    .transactionId(event.getTransactionId())
                    .status("FAILED")
                    .reason(e.getMessage())
                    .build());
        }
    }

    private void processWalletOperation(TransactionCreatedEvent event) {
        Long sourceUserId;
        Long targetUserId;

        // Determine Source and Target based on Type
        // TOPUP: System -> User
        // WITHDRAW: User -> System
        // TRANSFER: User -> Receiver
        switch (event.getType()) {
            case "TOPUP":
                sourceUserId = SYSTEM_USER_ID;
                targetUserId = event.getUserId();
                break;
            case "WITHDRAW":
                sourceUserId = event.getUserId();
                targetUserId = SYSTEM_USER_ID;
                break;
            case "TRANSFER":
                sourceUserId = event.getUserId();
                targetUserId = Long.parseLong(event.getReferenceId());
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + event.getType());
        }

        // Deadlock Avoidance: Lock in order
        Long firstLock = Math.min(sourceUserId, targetUserId);
        Long secondLock = Math.max(sourceUserId, targetUserId);

        Wallet sourceWallet = walletRepository.findByUserIdWithLock(firstLock)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + firstLock));
        Wallet targetWallet = walletRepository.findByUserIdWithLock(secondLock)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + secondLock));

        // Re-assign correctly because we just grabbed them by ID order
        if (sourceWallet.getUserId().equals(targetUserId)) {
            Wallet temp = sourceWallet;
            sourceWallet = targetWallet;
            targetWallet = temp;
        }

        // Validation
        if (sourceWallet.getStatus() == WalletStatus.FROZEN || targetWallet.getStatus() == WalletStatus.FROZEN) {
            throw new RuntimeException("One of the wallets is FROZEN");
        }

        // Check Balance (System wallet can go negative for TopUps)
        if (!sourceWallet.getUserId().equals(SYSTEM_USER_ID) && sourceWallet.getBalance().compareTo(event.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Execute Move
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(event.getAmount()));
        targetWallet.setBalance(targetWallet.getBalance().add(event.getAmount()));

        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);

        // Create Double-Entry Ledger
        createLedgerEntry(sourceWallet.getId(), event.getAmount(), LedgerEntryType.DEBIT, event.getTransactionId(), "Debit for " + event.getType());
        createLedgerEntry(targetWallet.getId(), event.getAmount(), LedgerEntryType.CREDIT, event.getTransactionId(), "Credit for " + event.getType());
    }

    private void createLedgerEntry(Long walletId, BigDecimal amount, LedgerEntryType type, String txId, String desc) {
        LedgerEntry entry = LedgerEntry.builder()
                .walletId(walletId)
                .amount(amount)
                .type(type)
                .transactionId(txId)
                .description(desc)
                .build();
        ledgerEntryRepository.save(entry);
    }
}
