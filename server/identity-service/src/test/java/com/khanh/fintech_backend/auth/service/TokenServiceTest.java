package com.khanh.fintech_backend.auth.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TokenServiceTest {

    @Test
    void issueTokens_returnsDemoTokens() {
        var svc = new TokenService();
        var user = new UserService.User();
        user.setId("u1");
        user.setUsername("demo");
        var tokens = svc.issueTokens(user);
        assertNotNull(tokens.getAccessToken());
        assertNotNull(tokens.getRefreshToken());
    }
}

