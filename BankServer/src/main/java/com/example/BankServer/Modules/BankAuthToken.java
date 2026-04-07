package com.example.BankServer.Modules;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_auth_token")
public class BankAuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  OAuth-style authorization token
    @Column(nullable = false, unique = true)
    private String token;

    //  User who authorized the operation
    @Column(nullable = false)
    private String userEmail;

    //  Bank account for which the token is valid
    @Column(nullable = false)
    private Long bankAccountId;

    //  Token expiry time
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // --- Constructors ---
    public BankAuthToken() {}

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}