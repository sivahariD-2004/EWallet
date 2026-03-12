package com.example.UserService.Controller;


import com.example.UserService.Modules.User;
import com.example.UserService.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }
    @PostMapping("/login")
    public User loginUser(@RequestBody User user){
        return userService.loginUser(user.getEmail(),user.getPassword());
    }
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);
        return "User deleted successfully";
    }
}
