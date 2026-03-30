package com.example.WalletService.Controller;

import com.example.WalletService.FeignClient.UserClient;
import com.example.WalletService.Modules.Wallet;
import com.example.WalletService.Service.WalletService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Value("${server.port}")
    private String port;
    @Autowired
    private UserClient userClient; //  ADD THIS



    @Autowired
    private Environment environment;


    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    private Long getLoggedInUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        var user = userClient.getUserByEmail(email);
        return user.getId();   // FIXED
    }
    // Create a new wallet (NO AUTH CHECK REQUIRED)
    @PostMapping("/create")
    public ResponseEntity<WalletResponse> create(@Valid @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createWallet(request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(WalletResponse.from(wallet));
    }

    // Get wallet by id (SECURED)
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> get(@PathVariable Long walletId) {
        Wallet wallet = walletService.getWallet(walletId);

        Long loggedInUserId = getLoggedInUserId();

        if (!wallet.getUserId().equals(loggedInUserId)) {
            throw new RuntimeException("Forbidden: Access denied");
        }

        return ResponseEntity.ok(WalletResponse.from(wallet));
    }

    // ⚠️ Optional: secure this if needed
    @GetMapping("/all")
    public ResponseEntity<List<WalletResponse>> getAll() {
        List<Wallet> wallets = walletService.getAllWallets();

        List<WalletResponse> response = wallets.stream()
                .map(WalletResponse::from)
                .toList();
        System.out.println("Handled by WalletService on port: " + port);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String test() {
        String port = environment.getProperty("local.server.port");
        return "Handled by WalletService on port: " + port;
    }

    // ✅ Deposit (SECURED)
    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<WalletResponse> deposit(
            @PathVariable Long walletId,
            @Valid @RequestBody AmountRequest request) {

        Wallet wallet = walletService.getWallet(walletId);

        Long loggedInUserId = getLoggedInUserId();
        System.out.println("Handled by WalletService on port: " + port);


        wallet = walletService.deposit(walletId, request.amount());
        return ResponseEntity.ok(WalletResponse.from(wallet));
    }

    // ✅ Withdraw (SECURED)
    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @PathVariable Long walletId,
            @Valid @RequestBody AmountRequest request) {

        Wallet wallet = walletService.getWallet(walletId);

        Long loggedInUserId = getLoggedInUserId();

        if (!wallet.getUserId().equals(loggedInUserId)) {
            throw new RuntimeException("Forbidden: Access denied");
        }

        wallet = walletService.withdraw(walletId, request.amount());
        return ResponseEntity.ok(WalletResponse.from(wallet));
    }

    // ✅ Block wallet (SECURED)
    @PostMapping("/{walletId}/block")
    public ResponseEntity<WalletResponse> block(@PathVariable Long walletId) {

        Wallet wallet = walletService.getWallet(walletId);

        Long loggedInUserId = getLoggedInUserId();

        if (!wallet.getUserId().equals(loggedInUserId)) {
            throw new RuntimeException("Forbidden: Access denied");
        }

        wallet = walletService.blockWallet(walletId);
        return ResponseEntity.ok(WalletResponse.from(wallet));
    }

    // ✅ Add money (SECURED)
    /*@PostMapping("/add-money")
    public Wallet addMoney(
            @RequestParam Long walletId,
            @RequestParam Long bankAccountId,
            @RequestParam BigDecimal amount
    ) {
        Wallet wallet = walletService.getWallet(walletId);

        Long loggedInUserId = getLoggedInUserId();

        if (!wallet.getUserId().equals(loggedInUserId)) {
            throw new RuntimeException("Forbidden: Access denied");
        }

        return walletService.addMoney(walletId, bankAccountId, amount);
    }
*/

    @PostMapping("/add-money")
    public Wallet addMoney(
            @RequestParam Long walletId,
            @RequestParam Long bankAccountId,
            @RequestParam BigDecimal amount,
            @RequestParam String token
    ) {

        Wallet wallet = walletService.getWallet(walletId);
        Long loggedInUserId = getLoggedInUserId();

        if (!wallet.getUserId().equals(loggedInUserId)) {
            throw new RuntimeException("Forbidden");
        }

        return walletService.addMoney(walletId, bankAccountId, amount, token);
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
                    w.getWalletId(),
                    w.getUserId(),
                    w.getBalance(),
                    w.getStatus() != null ? w.getStatus().name() : null,
                    w.getCreatedAt(),
                    w.getUpdatedAt(),
                    w.getVersion()
            );
        }
    }

    // --- Exception Handling ---

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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(RuntimeException ex) {
        return problem(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> problem(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}