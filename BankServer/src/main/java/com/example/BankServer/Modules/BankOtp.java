package com.example.BankServer.Modules;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_otp")
public class BankOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    // OTP is bound to specific bank account
    @Column(nullable = false)
    private Long bankAccountId;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // ---- Getters & Setters ----

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}