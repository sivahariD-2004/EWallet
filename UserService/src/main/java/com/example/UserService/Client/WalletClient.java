package com.example.UserService.Client;

import com.example.UserService.config.FeignAuthConfig;
import com.example.UserService.dto.CreateWalletRequest;
import com.example.UserService.dto.WalletResponse;
import jakarta.ws.rs.Path;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "WALLET-SERVICE",
        configuration = FeignAuthConfig.class
)
public interface WalletClient {

    @PostMapping("/wallets/create")
    WalletResponse createWallet(
            @RequestBody CreateWalletRequest request
    );

    @DeleteMapping("/wallets/{walletId}")
    void deleteWallet(@PathVariable("walletId") Long walletId );


}