package com.example.WalletService.Service;

import com.example.WalletService.FeignClient.BankClient;
import com.example.WalletService.Modules.Wallet;
import com.example.WalletService.Repository.WalletRepository;
import com.example.WalletService.status.WalletStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    private final BankClient bankClient;


    public WalletService(WalletRepository walletRepository,BankClient bankClient) {
        this.walletRepository = walletRepository;
        this.bankClient = bankClient;
    }


    @Transactional
    public Wallet addMoney(Long walletId, Long bankAccountId, BigDecimal amount) {

        validatePositive(amount);

        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found: " + walletId));

        ensureActive(wallet);

        //  Withdraw from Bank (remote call)
        bankClient.withdraw(bankAccountId, amount);

        //  Credit wallet only if bank withdrawal succeeds
        wallet.increase(amount);

        // persist wallet update
        return walletRepository.saveAndFlush(wallet);
    }

        // Creates a new wallet and writes it to the DB immediately
    @Transactional
    public Wallet createWallet(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        Wallet w = new Wallet(userId);
        return walletRepository.saveAndFlush(w);
    }

    // Deposits funds and persists the new balance
    @Transactional
    public Wallet deposit(Long walletId, BigDecimal amount) {
        validatePositive(amount);

        Wallet w = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found: " + walletId));

        ensureActive(w);
        w.increase(amount);

        // save & flush ensures DB is updated within this transaction
        return walletRepository.saveAndFlush(w);
    }

    // Withdraws funds and persists the new balance
    @Transactional
    public Wallet withdraw(Long walletId, BigDecimal amount) {
        validatePositive(amount);

        Wallet w = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found: " + walletId));

        ensureActive(w);

        if (w.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        w.decrease(amount);

        return walletRepository.saveAndFlush(w);
    }

    // Blocks the wallet and persists the new status
    @Transactional
    public Wallet blockWallet(Long walletId) {
        Wallet w = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found: " + walletId));

        w.block();

        return walletRepository.saveAndFlush(w);
    }
    @Transactional(readOnly = true)
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }
    // Read-only fetch (no write)
    @Transactional(readOnly = true)
    public Wallet getWallet(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found: " + walletId));
    }

    // --- Helpers ---
    private void validatePositive(BigDecimal amount) {
        if (Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }
    }

    private void ensureActive(Wallet wallet) {
        if (wallet.getStatus() == WalletStatus.BLOCKED) {
            throw new IllegalStateException("Wallet is blocked");
        }
    }
}