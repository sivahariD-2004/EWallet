package com.example.TransactionService.Controller;

import com.example.TransactionService.Modules.Transaction;
import com.example.TransactionService.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Transfer Money
    @PostMapping("/transfer")
    public Transaction transferMoney(@RequestBody Transaction transaction) {
        return transactionService.transferMoney(transaction);
    }

    // Transaction History
    @GetMapping("/history/{walletId}")
    public List<Transaction> getTransactionHistory(@PathVariable Long walletId){
        return transactionService.getTransactionHistory(walletId);
    }
}