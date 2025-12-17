package com.fintech.transaction.service;

import com.fintech.common.event.TransactionCreatedEvent;
import com.fintech.common.event.WalletProcessedEvent;
import com.fintech.transaction.dto.TransactionRequest;
import com.fintech.transaction.model.Transaction;
import com.fintech.transaction.model.TransactionStatus;
import com.fintech.transaction.model.TransactionType;
import com.fintech.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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
                .status(TransactionStatus.CREATED) // Initial state
                .referenceId(request.getReferenceId())
                .description(request.getDescription())
                .build();

        transaction = transactionRepository.save(transaction);
        
        // Publish Event
        TransactionCreatedEvent event = TransactionCreatedEvent.builder()
                .transactionId(transaction.getId().toString())
                .userId(transaction.getUserId())
                .amount(transaction.getAmount())
                .type(transaction.getType().name())
                .referenceId(transaction.getReferenceId())
                .build();

        kafkaTemplate.send("transaction-events", event);
        log.info("Published transaction event: {}", event);
        
        return transaction;
    }

    @KafkaListener(topics = "wallet-events", groupId = "transaction-group")
    @Transactional
    public void handleWalletEvent(WalletProcessedEvent event) {
        log.info("Received wallet event: {}", event);
        try {
            UUID txId = UUID.fromString(event.getTransactionId());
            Transaction transaction = transactionRepository.findById(txId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found: " + txId));

            if ("SUCCESS".equals(event.getStatus())) {
                transaction.setStatus(TransactionStatus.COMPLETED);
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
                // In a real SAGA, we might save the failure reason
            }
            transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Error processing wallet event: {}", e.getMessage());
        }
    }
}
