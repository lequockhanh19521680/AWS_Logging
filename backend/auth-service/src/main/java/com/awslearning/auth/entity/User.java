package com.awslearning.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  private UUID id;
  private String email;
  private String displayName;
  private String avatarUrl;
  @Column(columnDefinition = "TEXT")
  private String bio;
  private String provider;
  private String providerId;
  private String role;
  private Instant createdAt;
  private Instant updatedAt;
}

