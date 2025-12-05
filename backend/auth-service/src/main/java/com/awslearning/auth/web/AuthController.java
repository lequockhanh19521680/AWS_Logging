package com.awslearning.auth.web;

import com.awslearning.auth.entity.User;
import com.awslearning.auth.repo.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserRepository users;
  public AuthController(UserRepository users) { this.users = users; }

  @GetMapping("/health")
  public Map<String, Object> health() { return Map.of("status", "ok"); }

  @GetMapping("/profile/{id}")
  public User getProfile(@PathVariable UUID id) { return users.findById(id).orElseThrow(); }

  @PutMapping("/profile/{id}")
  public User updateProfile(@PathVariable UUID id, @RequestBody User incoming) {
    User u = users.findById(id).orElseThrow();
    u.setDisplayName(incoming.getDisplayName());
    u.setAvatarUrl(incoming.getAvatarUrl());
    u.setBio(incoming.getBio());
    u.setUpdatedAt(Instant.now());
    return users.save(u);
  }
}
