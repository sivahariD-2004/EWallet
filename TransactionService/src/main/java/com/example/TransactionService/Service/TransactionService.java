package com.example.TransactionService.Service;

import com.example.TransactionService.Client.UserClient;
import com.example.TransactionService.Modules.Transaction;
import com.example.TransactionService.Repository.TransactionRepository;
import com.example.TransactionService.Client.WalletClient;
import com.example.TransactionService.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletClient walletClient;

    @Autowired
    private UserClient userClient;

    public Transaction transferMoney(Transaction transaction) {

        boolean amountWithdrawn = false;

        try {

            // 1 Get logged-in user from JWT
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            // 2️ Get userId from UserService
            var user = userClient.getUserByEmail(email);
            Long loggedInUserId = user.id;

            // 3 Get sender wallet details
            var senderWallet = walletClient.getWallet(transaction.getSenderWalletId());

            // 4 Authorization check (VERY IMPORTANT)
            if (!senderWallet.userId.equals(loggedInUserId)) {
                throw new RuntimeException("Forbidden: You cannot access this wallet");
            }

            // 5️ Basic validations
            if (transaction.getSenderWalletId().equals(transaction.getReceiverWalletId())) {
                throw new RuntimeException("Sender and Receiver wallet cannot be the same");
            }

            if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Transfer amount must be greater than zero");
            }

            // 6️ Withdraw from sender
            walletClient.withdraw(
                    transaction.getSenderWalletId(),
                    new WalletClient.AmountRequest(transaction.getAmount())
            );

            amountWithdrawn = true;

            // 7 Deposit to receiver
            walletClient.deposit(
                    transaction.getReceiverWalletId(),
                    new WalletClient.AmountRequest(transaction.getAmount())
            );

            transaction.setStatus("SUCCESS");

        } catch (Exception e) {

            // 8 Compensation (Refund logic)
            if (amountWithdrawn) {
                try {
                    walletClient.deposit(
                            transaction.getSenderWalletId(),
                            new WalletClient.AmountRequest(transaction.getAmount())
                    );
                } catch (Exception refundException) {
                    System.out.println("Refund failed: " + refundException.getMessage());
                }

                transaction.setStatus("FAILED - RECEIVER ERROR (REFUNDED)");
            } else {
                transaction.setStatus("FAILED - SENDER ERROR");
            }
        }

        return transactionRepository.save(transaction);
    }

    // Transaction History

    public List<Transaction> getTransactionHistory(Long walletId) {

        // 1 Get logged-in user from JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // 2️ Get userId from UserService
        var user = userClient.getUserByEmail(email);
        Long loggedInUserId = user.id;

        // 3️ Get wallet details
        var wallet = walletClient.getWallet(walletId);

        // 4️ Authorization check
        if (!wallet.userId.equals(loggedInUserId)) {
            throw new ForbiddenException("You cannot view this wallet's transactions");
        }

        // 5️ Fetch transactions
        return transactionRepository
                .findBySenderWalletIdOrReceiverWalletId(walletId, walletId);
    }
}