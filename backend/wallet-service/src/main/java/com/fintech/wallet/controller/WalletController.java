package com.fintech.wallet.controller;

import com.fintech.wallet.dto.TransactionRequest;
import com.fintech.wallet.model.LedgerEntry;
import com.fintech.wallet.model.Wallet;
import com.fintech.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestParam Long userId) {
        return ResponseEntity.ok(walletService.createWallet(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWallet(userId));
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<LedgerEntry>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getLedgerHistory(userId));
    }

    @PostMapping("/transaction")
    public ResponseEntity<Wallet> processTransaction(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(walletService.processTransaction(request));
    }
}
