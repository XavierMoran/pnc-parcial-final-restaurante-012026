package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.AuthResponse;
import com.uca.pncparcialfinalrestaurante.dto.LoginRequest;
import com.uca.pncparcialfinalrestaurante.dto.RefreshTokenRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}