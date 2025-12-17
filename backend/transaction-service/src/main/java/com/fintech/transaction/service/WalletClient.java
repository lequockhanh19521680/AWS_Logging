package com.fintech.transaction.service;

import com.fintech.transaction.dto.WalletTransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WalletClient {

    private final RestTemplate restTemplate;

    @Value("${wallet.service.url:http://localhost:8082/api/wallets}")
    private String walletServiceUrl;

    public void processWalletTransaction(WalletTransactionRequest request) {
        restTemplate.postForEntity(walletServiceUrl + "/transaction", request, Void.class);
    }
}
