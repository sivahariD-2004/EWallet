package com.example.TransactionService.Repository;

import com.example.TransactionService.Modules.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}