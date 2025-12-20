package com.khanh.fintech_backend.auth.service;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

    public String issuePreAuthToken(UserService.User user) {
        return "preauth-demo";
    }

    public void sendOtp(UserService.User user) {
    }

    public UserService.User validatePreAuthToken(String preAuthToken) {
        return null;
    }

    public boolean verifyOtp(UserService.User user, String otp) {
        return false;
    }
}

