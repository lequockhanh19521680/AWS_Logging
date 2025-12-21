package com.khanh.fintech_backend.infrastructure.outbound.persistence;

import com.khanh.fintech_backend.application.ports.out.UserRepositoryPort;
import com.khanh.fintech_backend.domain.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepositoryPort {

    @Override
    public User findByUsername(String username) {
        // Stub implementation
        if ("admin".equals(username)) {
            return User.builder()
                    .id("1")
                    .username("admin")
                    .passwordHash("password") // In real app, verify hash
                    .twoFactorEnabled(true)
                    .build();
        }
        return null;
    }

    @Override
    public User save(User user) {
        return user;
    }
}
