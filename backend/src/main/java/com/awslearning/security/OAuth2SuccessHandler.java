package com.awslearning.security;

import com.awslearning.entity.User;
import com.awslearning.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
  private final UserRepository users;
  private final JwtTokenProvider jwt;

  public OAuth2SuccessHandler(UserRepository users, JwtTokenProvider jwt) {
    this.users = users;
    this.jwt = jwt;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
    String email = (String) principal.getAttributes().getOrDefault("email", "");
    String sub = (String) principal.getAttributes().getOrDefault("sub", "");
    String providerName = principal.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("GOOGLE");
    User.Provider provider = providerName.contains("github") ? User.Provider.GITHUB : User.Provider.GOOGLE;
    User user = users.findByProviderAndProviderId(provider, sub).orElseGet(() -> {
      User u = new User();
      u.setId(UUID.randomUUID());
      u.setEmail(email);
      u.setProvider(provider);
      u.setProviderId(sub);
      u.setRole("USER");
      u.setCreatedAt(Instant.now());
      return u;
    });
    users.save(user);
    String token = jwt.createToken(user.getId().toString(), Map.of("email", user.getEmail(), "role", user.getRole()));
    Cookie cookie = new Cookie("jwt", token);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    response.addCookie(cookie);
  }
}

