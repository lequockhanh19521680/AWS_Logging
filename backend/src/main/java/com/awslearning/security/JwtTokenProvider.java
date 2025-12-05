package com.awslearning.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {
  private final Key key;
  private final long expirationSeconds;

  public JwtTokenProvider(@Value("${security.jwt.secret}") String secret,
                          @Value("${security.jwt.expiration-seconds}") long expirationSeconds) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.expirationSeconds = expirationSeconds;
  }

  public String createToken(String subject, Map<String, Object> claims) {
    Instant now = Instant.now();
    return Jwts.builder()
        .setSubject(subject)
        .addClaims(claims)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }
}

