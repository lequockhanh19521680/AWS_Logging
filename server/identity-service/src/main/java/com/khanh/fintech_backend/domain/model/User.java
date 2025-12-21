package com.khanh.fintech_backend.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String id;
    private String username;
    private String passwordHash;
    private boolean twoFactorEnabled;
}
