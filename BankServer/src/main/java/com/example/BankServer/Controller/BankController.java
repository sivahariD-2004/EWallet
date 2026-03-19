package com.example.BankServer.Controller;

import com.example.BankServer.Modules.BankAccount;
import com.example.BankServer.Service.BankService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
public class BankController {

    private final BankService service;

    public BankController(BankService service) {
        this.service = service;
    }

    @PutMapping("/{id}/withdraw")
    public BankAccount withdraw(
            @PathVariable Long id,
            @RequestParam BigDecimal amount
    ) {
        return service.withdraw(id, amount);
    }
}
