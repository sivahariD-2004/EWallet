package com.example.WalletService.FeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "BANK-SERVICE")
public interface BankClient {

    // ✅ Step 1: Request OTP
    @PostMapping("/api/accounts/oauth/request-otp")
    void requestOtp(@RequestParam String email);

    // ✅ Step 2: Verify OTP & get authorization token
    @PostMapping("/api/accounts/oauth/verify-otp")
    String verifyOtp(
            @RequestParam String email,
            @RequestParam String otp
    );

    // ✅ Step 3: Withdraw using authorization token
    @PutMapping("/api/accounts/{id}/withdraw")
    void withdraw(
            @PathVariable("id") Long accountId,
            @RequestHeader("Authorization") String token,
            @RequestParam("amount") BigDecimal amount
    );
}