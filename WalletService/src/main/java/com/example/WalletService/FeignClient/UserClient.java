package com.example.WalletService.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "USER-SERVICE",
        path = "/users"          // base path exposed by user-service (adjust)
)
public interface UserClient {

    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") Long userId);

    // Add other endpoints as needed (POST/PUT/etc.)
    // @PostMapping("/...")
    // ResponseType doSomething(@RequestBody RequestType payload);

    // DTOs can be inner classes or separate files
    class UserResponse {
        private Long id;
        private String email;
        private String status;
        // getters/setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
