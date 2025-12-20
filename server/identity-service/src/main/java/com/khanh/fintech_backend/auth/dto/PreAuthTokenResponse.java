package com.khanh.fintech_backend.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreAuthTokenResponse {
    private String preAuthToken;
    private int expiresIn;
}

