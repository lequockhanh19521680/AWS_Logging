package com.khanh.fintech_backend.auth.controller;

import com.khanh.fintech_backend.auth.dto.LoginRequest;
import com.khanh.fintech_backend.auth.service.OtpService;
import com.khanh.fintech_backend.auth.service.TokenService;
import com.khanh.fintech_backend.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerUnitTest {

    @Test
    void login_invalid_returns401() {
        var ctrl = new AuthController(new UserService(), new TokenService(), new OtpService());
        var req = new LoginRequest();
        req.setUsername("bad");
        req.setPassword("bad");
        ResponseEntity<?> resp = ctrl.login(req);
        assertEquals(401, resp.getStatusCode().value());
    }
}

