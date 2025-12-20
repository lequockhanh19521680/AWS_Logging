package com.khanh.fintech_backend.auth.controller;

import com.khanh.fintech_backend.auth.dto.*;
import com.khanh.fintech_backend.auth.service.OtpService;
import com.khanh.fintech_backend.auth.service.TokenService;
import com.khanh.fintech_backend.auth.service.UserService;
import jakarta.validation.Valid;
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

    private final UserService userService;
    private final TokenService tokenService;
    private final OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest req) {
        var user = userService.findByUsername(req.getUsername());
        if (user == null || !userService.verifyPassword(user, req.getPassword())) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.builder().status(401).message("INVALID_CREDENTIALS").data(null).build());
        }
        if (userService.isTwoFactorEnabled(user)) {
            var preAuth = otpService.issuePreAuthToken(user);
            otpService.sendOtp(user);
            var data = PreAuthTokenResponse.builder().preAuthToken(preAuth).expiresIn(300).build();
            return ResponseEntity.ok(ApiResponse.builder().status(200).message("OTP_SENT").data(data).build());
        }
        var tokens = tokenService.issueTokens(user);
        return ResponseEntity.ok(ApiResponse.builder().status(200).message("OK").data(tokens).build());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(@Valid @RequestBody OtpVerifyRequest req) {
        var user = otpService.validatePreAuthToken(req.getPreAuthToken());
        if (user == null || !otpService.verifyOtp(user, req.getOtp())) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.builder().status(401).message("OTP_INVALID").data(null).build());
        }
        var tokens = tokenService.issueTokens(user);
        return ResponseEntity.ok(ApiResponse.builder().status(200).message("OK").data(tokens).build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refresh(@RequestBody String refreshToken) {
        var access = tokenService.refreshAccessToken(refreshToken);
        if (access == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.builder().status(401).message("TOKEN_EXPIRED").data(null).build());
        }
        var data = AuthTokens.builder().accessToken(access).refreshToken(refreshToken).build();
        return ResponseEntity.ok(ApiResponse.builder().status(200).message("OK").data(data).build());
    }
}

