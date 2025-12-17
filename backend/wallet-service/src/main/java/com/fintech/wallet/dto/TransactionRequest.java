package com.fintech.wallet.dto;

import com.fintech.wallet.model.LedgerEntryType;
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
    private LedgerEntryType type;
    private String transactionId;
    private String description;
}
