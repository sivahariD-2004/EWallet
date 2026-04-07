package com.example.TransactionService.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@FeignClient(
        name = "WALLET-SERVICE",
        configuration = com.example.TransactionService.Config.FeignAuthConfig.class
)
public interface WalletClient {

    @PostMapping("/wallets/{walletId}/withdraw")
    Object withdraw(@PathVariable("walletId") Long walletId,
                    @RequestBody AmountRequest request);

    @PostMapping("/wallets/{walletId}/deposit")
    Object deposit(@PathVariable("walletId") Long walletId,
                   @RequestBody AmountRequest request);

    //  ADD THIS METHOD
    @GetMapping("/wallets/{walletId}")
    WalletResponse getWallet(@PathVariable("walletId") Long walletId);

    // DTO for amount
    class AmountRequest {
        public BigDecimal amount;
        public AmountRequest(BigDecimal amount) {
            this.amount = amount;
        }
    }

    // ADD THIS DTO
    class WalletResponse {
        public Long walletId;
        public Long userId;
        public BigDecimal balance;
    }
}