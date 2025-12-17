package com.fintech.transaction.service;

import com.fintech.transaction.dto.TransactionRequest;
import com.fintech.transaction.dto.WalletTransactionRequest;
import com.fintech.transaction.model.Transaction;
import com.fintech.transaction.model.TransactionStatus;
import com.fintech.transaction.model.TransactionType;
import com.fintech.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletClient walletClient;

    public List<Transaction> getHistory(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Transactional
    public Transaction createTransaction(TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .currency("USD")
                .type(request.getType())
                .status(TransactionStatus.CREATED)
                .referenceId(request.getReferenceId())
                .description(request.getDescription())
                .build();

        transaction = transactionRepository.save(transaction);
        
        try {
            processTransaction(transaction);
            transaction.setStatus(TransactionStatus.COMPLETED);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            // In a real system, we might need reversal if partial failure
        }
        
        return transactionRepository.save(transaction);
    }

    private void processTransaction(Transaction tx) {
        String txId = tx.getId().toString() + "-" + UUID.randomUUID().toString();
        
        if (tx.getType() == TransactionType.TOPUP) {
            walletClient.processWalletTransaction(WalletTransactionRequest.builder()
                    .userId(tx.getUserId())
                    .amount(tx.getAmount())
                    .type("CREDIT")
                    .transactionId(txId)
                    .description("Topup: " + tx.getDescription())
                    .build());
        } else if (tx.getType() == TransactionType.WITHDRAW) {
            walletClient.processWalletTransaction(WalletTransactionRequest.builder()
                    .userId(tx.getUserId())
                    .amount(tx.getAmount())
                    .type("DEBIT")
                    .transactionId(txId)
                    .description("Withdraw: " + tx.getDescription())
                    .build());
        } else if (tx.getType() == TransactionType.TRANSFER) {
            // Debit Sender
            walletClient.processWalletTransaction(WalletTransactionRequest.builder()
                    .userId(tx.getUserId())
                    .amount(tx.getAmount())
                    .type("DEBIT")
                    .transactionId(txId + "-DR")
                    .description("Transfer Out to " + tx.getReferenceId())
                    .build());
            
            // Credit Receiver
            Long receiverId = Long.parseLong(tx.getReferenceId());
            walletClient.processWalletTransaction(WalletTransactionRequest.builder()
                    .userId(receiverId)
                    .amount(tx.getAmount())
                    .type("CREDIT")
                    .transactionId(txId + "-CR")
                    .description("Transfer In from " + tx.getUserId())
                    .build());
        }
    }
}
