package com.fintech.wallet.service;

import com.fintech.wallet.dto.TransactionRequest;
import com.fintech.wallet.model.LedgerEntry;
import com.fintech.wallet.model.LedgerEntryType;
import com.fintech.wallet.model.Wallet;
import com.fintech.wallet.model.WalletStatus;
import com.fintech.wallet.repository.LedgerEntryRepository;
import com.fintech.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

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

    @Transactional
    public Wallet processTransaction(TransactionRequest request) {
        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + request.getUserId()));

        if (wallet.getStatus() == WalletStatus.FROZEN) {
            throw new RuntimeException("Wallet is FROZEN");
        }

        if (request.getType() == LedgerEntryType.DEBIT) {
            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new RuntimeException("Insufficient funds");
            }
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        } else {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        }

        LedgerEntry entry = LedgerEntry.builder()
                .walletId(wallet.getId())
                .amount(request.getAmount())
                .type(request.getType())
                .transactionId(request.getTransactionId())
                .description(request.getDescription())
                .build();

        ledgerEntryRepository.save(entry);
        return walletRepository.save(wallet);
    }
}
