package com.awslearning.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
  @Enumerated(EnumType.STRING)
  private Provider provider;
  private String providerId;
  private String role;
  private Instant createdAt;

  public enum Provider { GOOGLE, GITHUB, FACEBOOK }
}

