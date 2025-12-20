package com.khanh.fintech_backend.auth.service;

import com.khanh.fintech_backend.auth.dto.AuthTokens;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    public AuthTokens issueTokens(UserService.User user) {
        return AuthTokens.builder()
                .accessToken("access-demo")
                .refreshToken("refresh-demo")
                .build();
    }

    public String refreshAccessToken(String refreshToken) {
        return "access-demo";
    }
}

