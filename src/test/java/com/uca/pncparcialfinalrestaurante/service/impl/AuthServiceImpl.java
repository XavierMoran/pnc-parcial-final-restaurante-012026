package com.uca.pncparcialfinalrestaurante.service.impl;

import com.uca.pncparcialfinalrestaurante.dto.AuthResponse;
import com.uca.pncparcialfinalrestaurante.dto.LoginRequest;
import com.uca.pncparcialfinalrestaurante.dto.RefreshTokenRequest;
import com.uca.pncparcialfinalrestaurante.entity.RefreshToken;
import com.uca.pncparcialfinalrestaurante.entity.User;
import com.uca.pncparcialfinalrestaurante.exception.BusinessRuleException;
import com.uca.pncparcialfinalrestaurante.repository.RefreshTokenRepository;
import com.uca.pncparcialfinalrestaurante.repository.UserRepository;
import com.uca.pncparcialfinalrestaurante.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessRuleException("Usuario no encontrado"));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshTokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .revoked(false)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessRuleException("Refresh token inválido"));

        if (refreshToken.getRevoked()) {
            throw new BusinessRuleException("Refresh token revocado");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Refresh token expirado");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(refreshToken.getUser());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }
}