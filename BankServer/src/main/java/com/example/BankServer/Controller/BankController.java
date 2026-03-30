package com.example.BankServer.Controller;

import com.example.BankServer.Modules.BankAccount;
import com.example.BankServer.Modules.BankAuthToken;
import com.example.BankServer.Modules.BankOtp;
import com.example.BankServer.Repository.BankAuthTokenRepository;
import com.example.BankServer.Repository.BankOtpRepository;
import com.example.BankServer.Service.BankService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class BankController {

    private final BankService service;
    private final BankOtpRepository otpRepository;
    private final BankAuthTokenRepository tokenRepository;

    public BankController(
            BankService service,
            BankOtpRepository otpRepository,
            BankAuthTokenRepository tokenRepository) {
        this.service = service;
        this.otpRepository = otpRepository;
        this.tokenRepository = tokenRepository;
    }

    // ✅ STEP 1: REQUEST OTP (OAuth Authorization Request)
    @PostMapping("/oauth/request-otp")
    public String requestOtp(@RequestParam String email) {

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        BankOtp bankOtp = new BankOtp();
        bankOtp.setUserEmail(email);
        bankOtp.setOtp(otp);
        bankOtp.setExpiresAt(LocalDateTime.now().plusMinutes(2));
        bankOtp.setUsed(false);

        otpRepository.save(bankOtp);

        // Mock email sending
        System.out.println("✅ OTP sent to email: " + email + " OTP: " + otp);

        return "OTP sent to registered email";
    }

    // ✅ STEP 2: VERIFY OTP & ISSUE ACCESS TOKEN
    @PostMapping("/oauth/verify-otp")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {

        BankOtp bankOtp = otpRepository
                .findByUserEmailAndOtpAndUsedFalse(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (bankOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        bankOtp.setUsed(true);
        otpRepository.save(bankOtp);

        String accessToken = UUID.randomUUID().toString();

        BankAuthToken token = new BankAuthToken();
        token.setUserEmail(email);
        token.setToken(accessToken);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        tokenRepository.save(token);

        return accessToken;
    }

    // ✅ STEP 3: SECURED WITHDRAW (OAuth Resource Access)
    @PutMapping("/{id}/withdraw")
    public BankAccount withdraw(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam BigDecimal amount) {

        //String token = authHeader.replace("Bearer ", "");
        String token = authHeader.split(",")[0].replace("Bearer ", "").trim();
        System.out.println("Authorization header received in Bank: " + authHeader);
        BankAuthToken authToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid authorization token"));

        if (authToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Authorization token expired");
        }

        return service.withdraw(id, amount);
    }
}