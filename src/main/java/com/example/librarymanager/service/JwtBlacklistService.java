package com.example.librarymanager.service;

public interface JwtBlacklistService {
    void blacklistAccessToken(String accessToken);

    void blacklistRefreshToken(String refreshToken);

    boolean isTokenBlocked(String token);
}
