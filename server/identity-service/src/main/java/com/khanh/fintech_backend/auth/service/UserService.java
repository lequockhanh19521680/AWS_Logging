package com.khanh.fintech_backend.auth.service;

import lombok.Data;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Data
    public static class User {
        private String id;
        private String username;
        private boolean twoFactorEnabled;
        private String passwordHash;
    }

    public User findByUsername(String username) {
        return null;
    }

    public boolean verifyPassword(User user, String rawPassword) {
        return false;
    }

    public boolean isTwoFactorEnabled(User user) {
        return user != null && user.twoFactorEnabled;
    }
}

