package com.fintech.transaction.dto;

import com.fintech.transaction.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private Long userId;
    private BigDecimal amount;
    private TransactionType type;
    private String referenceId; // For TRANSFER (target user id)
    private String description;
}
