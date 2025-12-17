package com.fintech.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreatedEvent {
    private String transactionId;
    private Long userId;
    private BigDecimal amount;
    private String type; // TOPUP, WITHDRAW, TRANSFER
    private String referenceId; // For TRANSFER (receiverId)
}
