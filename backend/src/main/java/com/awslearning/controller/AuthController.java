package com.awslearning.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class AuthController {
  @GetMapping("/api/auth/me")
  public Map<String, Object> me(@AuthenticationPrincipal User principal) {
    if (principal == null) return Map.of("authenticated", false);
    return Map.of("authenticated", true, "subject", principal.getUsername());
  }
}

