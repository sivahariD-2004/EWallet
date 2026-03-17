package com.example.TransactionService.Service;

import com.example.TransactionService.Modules.Transaction;
import com.example.TransactionService.Repository.TransactionRepository;
import com.example.TransactionService.Client.WalletClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletClient walletClient;

    public Transaction transferMoney(Transaction transaction) {

        boolean amountWithdrawn = false;

        try {

            // 1️⃣ Validation

            if(transaction.getSenderWalletId().equals(transaction.getReceiverWalletId())){
                throw new RuntimeException("Sender and Receiver wallet cannot be the same");
            }

            if(transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0){
                throw new RuntimeException("Transfer amount must be greater than zero");
            }

            // 2️⃣ Withdraw from sender wallet

            walletClient.withdraw(
                    transaction.getSenderWalletId(),
                    new WalletClient.AmountRequest(transaction.getAmount())
            );

            amountWithdrawn = true;

            // 3️⃣ Deposit to receiver wallet

            walletClient.deposit(
                    transaction.getReceiverWalletId(),
                    new WalletClient.AmountRequest(transaction.getAmount())
            );

            transaction.setStatus("SUCCESS");

        } catch (Exception e) {

            // 4️⃣ If receiver side fails → refund sender

            if(amountWithdrawn){

                try{
                    walletClient.deposit(
                            transaction.getSenderWalletId(),
                            new WalletClient.AmountRequest(transaction.getAmount())
                    );
                }catch(Exception refundException){
                    System.out.println("Refund failed: " + refundException.getMessage());
                }

                transaction.setStatus("FAILED - RECEIVER ERROR (REFUNDED)");
            }
            else{
                // sender side failure
                transaction.setStatus("FAILED - SENDER ERROR");
            }
        }

        return transactionRepository.save(transaction);
    }

    // Transaction History

    public List<Transaction> getTransactionHistory(Long walletId){

        return transactionRepository
                .findBySenderWalletIdOrReceiverWalletId(walletId, walletId);
    }
}