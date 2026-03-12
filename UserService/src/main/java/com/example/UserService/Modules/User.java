package com.example.UserService.Modules;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String password;

    private String email;

    private String phone;

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    public User(Long userId, String email, String password, String phone) {
        this.userId = userId;
        this.password = password;
        this.email = email;
//        thffnhfs
        this.phone = phone;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}