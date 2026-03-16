package com.example.TransactionService.Service;



import com.example.TransactionService.Modules.Transaction;
import com.example.TransactionService.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction createTransaction(Transaction transaction){
        transaction.setStatus("SUCCESS");
        return transactionRepository.save(transaction);
    }
}
