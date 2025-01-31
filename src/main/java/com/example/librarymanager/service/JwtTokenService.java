package com.example.librarymanager.service;

public interface JwtTokenService {
    void blacklistAccessToken(String accessToken);

    void blacklistRefreshToken(String refreshToken);

    boolean isTokenAllowed(String token);
}
