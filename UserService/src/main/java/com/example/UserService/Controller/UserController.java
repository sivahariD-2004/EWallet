package com.example.UserService.Controller;

import com.example.UserService.Modules.User;
import com.example.UserService.Service.UserService;
import com.example.UserService.Security.JwtUtil;
import com.example.UserService.dto.*;
import com.example.UserService.exception.ForbiddenException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest req) {
        User user = new User();
        user.setEmail(req.email());
        user.setPassword(req.password()); // will be hashed in service
        user.setPhone(req.phone());       // keep only existing fields

        User saved = userService.registerUser(user);

        // Update your response DTO accordingly (no name)
        UserResponse resp = new UserResponse(saved.getUserId(), saved.getEmail(), saved.getPhone());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest req) {
        var loggedUser = userService.loginUser(req.email(), req.password());
        if (loggedUser != null) {
            String token = jwtUtil.generateToken(loggedUser.getEmail());
            return ResponseEntity.ok(new TokenResponse(token));
        }
        // standardized 401 for bad credentials
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("INVALID_CREDENTIALS", "Email or password is incorrect"));
    }

    @GetMapping("/email/{email}")
    public UserResponse getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return new UserResponse(user.getUserId(), user.getEmail(), user.getPhone());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        // 1️ Get logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // 2 Fetch user
        User loggedInUser = userService.getUserByEmail(email);

        // 3️ Authorization check
        if (!loggedInUser.getUserId().equals(id)) {
            throw new ForbiddenException("You cannot delete another user");
        }

        // 4️ Delete
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}