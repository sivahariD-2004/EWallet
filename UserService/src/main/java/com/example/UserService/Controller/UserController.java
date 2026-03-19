package com.example.UserService.Controller;

import com.example.UserService.Modules.User;
import com.example.UserService.Service.UserService;
import com.example.UserService.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user){

        User loggedUser = userService.loginUser(user.getEmail(), user.getPassword());

        if(loggedUser != null){
            return jwtUtil.generateToken(loggedUser.getEmail());
        }

        throw new RuntimeException("Invalid credentials");
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);
        return "User deleted successfully";
    }
}