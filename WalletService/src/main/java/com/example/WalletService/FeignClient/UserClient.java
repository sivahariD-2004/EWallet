package com.example.WalletService.FeignClient;

import com.example.WalletService.Config.UserJwtFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "USER-SERVICE",
        path = "/users",
        configuration = UserJwtFeignConfig.class   // ✅ IMPORTANT (token forwarding)
)
public interface UserClient {

    // ❌ Old method (optional to keep)
    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") Long userId);

    // ✅ ADD THIS METHOD (VERY IMPORTANT)
    @GetMapping("/email/{email}")
    UserResponse getUserByEmail(@PathVariable("email") String email);

    // DTO
    class UserResponse {
        private Long id;
        private String email;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}