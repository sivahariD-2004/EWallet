package com.example.BankServer.Repository;


import com.example.BankServer.Modules.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}