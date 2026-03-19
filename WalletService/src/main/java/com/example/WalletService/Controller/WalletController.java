package com.example.WalletService.Controller;

import com.example.WalletService.Modules.Wallet;
import com.example.WalletService.Service.WalletService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wallets")
@Validated
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // Create a new wallet for a user
    @PostMapping
    public ResponseEntity<WalletResponse> create(@Valid @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createWallet(request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(WalletResponse.from(wallet));
    }

    // Get wallet by id
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> get(@PathVariable Long walletId) {
        Wallet wallet = walletService.getWallet(walletId);
        return ResponseEntity.ok(WalletResponse.from(wallet));
    }

    @GetMapping("/all")
    public ResponseEntity<List<WalletResponse>> getAll() {
        System.out.println("🔥 /wallets/all HIT");
        List<Wallet> wallets = walletService.getAllWallets();
        List<WalletResponse> response = wallets.stream()
                .map(WalletResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }
    // Deposit to wallet
    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<WalletResponse> deposit(
            @PathVariable Long walletId,
            @Valid @RequestBody AmountRequest request) {

        Wallet wallet = walletService.deposit(walletId, request.amount());
        return ResponseEntity.ok(WalletResponse.from(wallet));
    }

    // Withdraw from wallet
    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @PathVariable Long walletId,
            @Valid @RequestBody AmountRequest request) {

        Wallet wallet = walletService.withdraw(walletId, request.amount());
        return ResponseEntity.ok(WalletResponse.from(wallet));
    }

    // Block wallet
    @PostMapping("/{walletId}/block")
    public ResponseEntity<WalletResponse> block(@PathVariable Long walletId) {
        Wallet wallet = walletService.blockWallet(walletId);
        return ResponseEntity.ok(WalletResponse.from(wallet));
    }

    @PostMapping("/add-money")
    public Wallet addMoney(
            @RequestParam Long walletId,
            @RequestParam Long bankAccountId,
            @RequestParam BigDecimal amount
    ) {
        return walletService.addMoney(walletId, bankAccountId, amount);
    }
    // --- DTOs ---

    public record CreateWalletRequest(
            @NotNull(message = "userId must not be null")
            Long userId
    ) {}

    public record AmountRequest(
            @NotNull(message = "amount must not be null")
            @DecimalMin(value = "0.00", inclusive = false, message = "amount must be > 0")
            BigDecimal amount
    ) {}

    public record WalletResponse(
            Long walletId,
            Long userId,
            BigDecimal balance,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long version
    ) {
        public static WalletResponse from(Wallet w) {
            return new WalletResponse(
                    w.getWalletId(),            // <-- FIXED: use getWalletId()
                    w.getUserId(),
                    w.getBalance(),
                    w.getStatus() != null ? w.getStatus().name() : null,
                    w.getCreatedAt(),
                    w.getUpdatedAt(),
                    w.getVersion()
            );
        }
    }

    // --- Local Exception Mapping ---

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex) {
        return problem(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> problem(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
