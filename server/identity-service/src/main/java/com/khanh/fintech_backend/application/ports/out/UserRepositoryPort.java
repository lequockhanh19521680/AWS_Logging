package com.khanh.fintech_backend.application.ports.out;

import com.khanh.fintech_backend.domain.model.User;

public interface UserRepositoryPort {
    User findByUsername(String username);
    User save(User user);
}
