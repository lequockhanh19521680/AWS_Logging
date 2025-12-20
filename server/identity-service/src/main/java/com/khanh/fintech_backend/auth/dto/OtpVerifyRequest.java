package com.khanh.fintech_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerifyRequest {
    @NotBlank
    private String preAuthToken;
    @NotBlank
    private String otp;
}

