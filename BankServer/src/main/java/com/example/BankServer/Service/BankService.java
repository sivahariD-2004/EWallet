package com.example.BankServer.Service;


import com.example.BankServer.Modules.BankAccount;
import com.example.BankServer.Repository.BankAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class BankService {

    private final BankAccountRepository repository;

    public BankService(BankAccountRepository repository) {
        this.repository = repository;
    }

    // Withdraw money from account
    public BankAccount withdraw(Long accountId, BigDecimal amount) {


        // Check if account exists first
        BankAccount account = repository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));


        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        return repository.save(account);
    }

    // (Optional) Get account details
    public BankAccount getAccount(Long accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}