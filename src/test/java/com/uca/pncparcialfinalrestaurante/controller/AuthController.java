package com.uca.pncparcialfinalrestaurante.controller;

import com.uca.pncparcialfinalrestaurante.dto.AuthResponse;
import com.uca.pncparcialfinalrestaurante.dto.LoginRequest;
import com.uca.pncparcialfinalrestaurante.dto.RefreshTokenRequest;
import com.uca.pncparcialfinalrestaurante.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }
}