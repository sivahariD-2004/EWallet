package com.example.TransactionService.Modules;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private Long senderWalletId;

    private Long receiverWalletId;

    private BigDecimal amount;

    private String status;

    private LocalDateTime dateTime;

    public Transaction() {
        this.dateTime = LocalDateTime.now();
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getSenderWalletId() {
        return senderWalletId;
    }

    public Long getReceiverWalletId() {
        return receiverWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setSenderWalletId(Long senderWalletId) {
        this.senderWalletId = senderWalletId;
    }

    public void setReceiverWalletId(Long receiverWalletId) {
        this.receiverWalletId = receiverWalletId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}