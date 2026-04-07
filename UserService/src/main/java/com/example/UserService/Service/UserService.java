package com.example.UserService.Service;

import com.example.UserService.dto.CreateWalletRequest;
import com.example.UserService.Modules.User;
import com.example.UserService.Repository.UserRepository;
import com.example.UserService.Client.WalletClient;
import com.example.UserService.exception.DuplicateEmailException;
import com.example.UserService.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder; //  injected
    private final WalletClient walletClient;


    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            WalletClient walletClient
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletClient = walletClient;
    }


    public User registerUser(User user) {
        //  duplicate email check
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        //  hash password before save
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        walletClient.createWallet(
                new CreateWalletRequest(savedUser.getUserId())
        );

        return savedUser;
    }

    public User loginUser(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        }
        return null; // controller will map to 401
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
        walletClient.deleteWallet(userId);
    }
}