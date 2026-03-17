package com.example.TransactionService.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {

    @PostMapping("/wallets/{walletId}/withdraw")
    Object withdraw(
            @PathVariable("walletId") Long walletId,
            @RequestBody AmountRequest request
    );

    @PostMapping("/wallets/{walletId}/deposit")
    Object deposit(
            @PathVariable("walletId") Long walletId,
            @RequestBody AmountRequest request
    );

    class AmountRequest {
        public BigDecimal amount;

        public AmountRequest(BigDecimal amount) {
            this.amount = amount;
        }
    }
}