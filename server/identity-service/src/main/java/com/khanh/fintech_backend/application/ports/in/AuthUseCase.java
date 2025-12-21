package com.khanh.fintech_backend.application.ports.in;

import com.khanh.fintech_backend.auth.dto.LoginRequest;
import com.khanh.fintech_backend.auth.dto.OtpVerifyRequest;
import com.khanh.fintech_backend.auth.dto.ApiResponse;

public interface AuthUseCase {
    ApiResponse<?> login(LoginRequest request);
    ApiResponse<?> verifyOtp(OtpVerifyRequest request);
    ApiResponse<?> refresh(String refreshToken);
}
