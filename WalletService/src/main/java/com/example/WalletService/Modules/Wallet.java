package com.example.WalletService.Modules;

import com.example.WalletService.status.WalletStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @Column(nullable = false)
    private Long userId; // FK reference

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus status = WalletStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version; // optimistic locking

    // --- JPA lifecycle hooks ---
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Constructors ---
    public Wallet() {}

    public Wallet(Long userId) {
        this.userId = userId;
    }

    // --- Getters & Setters ---
    public Long getWalletId() { return walletId; }
    public Long getUserId() { return userId; }
    public BigDecimal getBalance() { return balance; }
    public WalletStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setStatus(WalletStatus status) { this.status = status; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    // Domain helpers (no DB write here; persistence is done in service)
    public void increase(BigDecimal amount) { this.balance = this.balance.add(amount); }
    public void decrease(BigDecimal amount) { this.balance = this.balance.subtract(amount); }
    public void block() { this.status = WalletStatus.BLOCKED; }
}