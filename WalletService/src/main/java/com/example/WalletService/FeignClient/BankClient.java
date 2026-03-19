package com.example.WalletService.FeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "BANK-SERVICE")
public interface BankClient {

    @PutMapping("/api/accounts/{id}/withdraw")
    void withdraw(
            @PathVariable("id") Long accountId,
            @RequestParam("amount") BigDecimal amount
    );
}

