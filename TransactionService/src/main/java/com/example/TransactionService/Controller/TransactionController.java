package com.example.TransactionService.Controller;

import com.example.TransactionService.Modules.Transaction;
import com.example.TransactionService.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transfer")
    public Transaction transferMoney(@RequestBody Transaction transaction){
        return transactionService.createTransaction(transaction);
    }
}