package com.fintech.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionRequest {
    private Long userId;
    private BigDecimal amount;
    private String type; // CREDIT/DEBIT
    private String transactionId;
    private String description;
}
