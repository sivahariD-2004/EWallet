package com.example.UserService.Service;


import com.example.UserService.Modules.User;
import com.example.UserService.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User loginUser(String email,String password){

        User user = userRepository.findByEmail(email);

        if(user != null && user.getPassword().equals(password)){
            return user;
        }

        return null;
    }
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}