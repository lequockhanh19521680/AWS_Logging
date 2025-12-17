package com.fintech.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletProcessedEvent {
    private String transactionId;
    private String status; // SUCCESS, FAILED
    private String reason;
}
