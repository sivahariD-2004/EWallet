package com.example.UserService.Client;

import com.example.UserService.dto.CreateWalletRequest;
import com.example.UserService.dto.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "WALLET-SERVICE"

)
public interface WalletClient {

    @PostMapping("/wallets/create")
    WalletResponse createWallet(
            @RequestBody CreateWalletRequest request
    );


}