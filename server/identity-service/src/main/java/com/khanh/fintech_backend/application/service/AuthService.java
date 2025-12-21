package com.khanh.fintech_backend.application.service;

import com.khanh.fintech_backend.application.ports.in.AuthUseCase;
import com.khanh.fintech_backend.application.ports.out.UserRepositoryPort;
import com.khanh.fintech_backend.auth.dto.*;
import com.khanh.fintech_backend.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    public ApiResponse<?> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null || !request.getPassword().equals("password")) { // Mock check
            return ApiResponse.builder().status(401).message("INVALID_CREDENTIALS").build();
        }
        
        if (user.isTwoFactorEnabled()) {
             // Logic 2FA here (simplified for refactor demo)
             return ApiResponse.builder().status(200).message("OTP_SENT").build();
        }

        return ApiResponse.builder()
                .status(200)
                .message("OK")
                .data(AuthTokens.builder().accessToken("access-hex").refreshToken("refresh-hex").build())
                .build();
    }

    @Override
    public ApiResponse<?> verifyOtp(OtpVerifyRequest request) {
        // Logic verify OTP
        return ApiResponse.builder()
                .status(200)
                .message("OK")
                .data(AuthTokens.builder().accessToken("access-hex").refreshToken("refresh-hex").build())
                .build();
    }

    @Override
    public ApiResponse<?> refresh(String refreshToken) {
        return ApiResponse.builder()
                .status(200)
                .message("OK")
                .data(AuthTokens.builder().accessToken("new-access").refreshToken(refreshToken).build())
                .build();
    }
}
