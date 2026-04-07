package com.example.TransactionService.Client;

import com.example.TransactionService.Config.FeignAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "USER-SERVICE",
        configuration = FeignAuthConfig.class   // IMPORTANT (token forwarding)
)
public interface UserClient {

    @GetMapping("/users/email/{email}")
    UserResponse getUserByEmail(@PathVariable("email") String email);

    class UserResponse {
        public Long id;
        public String email;
        public String phone;
    }
}