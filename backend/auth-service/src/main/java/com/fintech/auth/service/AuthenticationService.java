package com.fintech.auth.service;

import com.fintech.auth.config.JwtService;
import com.fintech.auth.dto.AuthenticationRequest;
import com.fintech.auth.dto.AuthenticationResponse;
import com.fintech.auth.dto.RegisterRequest;
import com.fintech.auth.model.KycStatus;
import com.fintech.auth.model.Role;
import com.fintech.auth.model.User;
import com.fintech.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        var user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .kycStatus(KycStatus.PENDING)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(java.util.Map.of("userId", user.getId()), user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(java.util.Map.of("userId", user.getId()), user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
