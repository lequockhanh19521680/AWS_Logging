package com.khanh.fintech_backend.infrastructure.inbound.rest;

import com.khanh.fintech_backend.application.ports.in.AuthUseCase;
import com.khanh.fintech_backend.auth.dto.ApiResponse;
import com.khanh.fintech_backend.auth.dto.LoginRequest;
import com.khanh.fintech_backend.auth.dto.OtpVerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authUseCase.login(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(@RequestBody OtpVerifyRequest request) {
        return ResponseEntity.ok(authUseCase.verifyOtp(request));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authUseCase.refresh(refreshToken));
    }
}
