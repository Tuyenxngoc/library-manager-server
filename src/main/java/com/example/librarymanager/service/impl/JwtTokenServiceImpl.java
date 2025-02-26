package com.example.librarymanager.service.impl;

import com.example.librarymanager.security.jwt.JwtTokenProvider;
import com.example.librarymanager.service.JwtTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtTokenServiceImpl implements JwtTokenService {

    RedisTemplate<String, Object> redisTemplate;

    JwtTokenProvider jwtTokenProvider;

    @Override
    public void blacklistAccessToken(String accessToken) {
        redisTemplate.opsForValue().set(accessToken, "blacklisted", jwtTokenProvider.getRemainingTime(accessToken), TimeUnit.MILLISECONDS);
    }

    @Override
    public void blacklistRefreshToken(String refreshToken) {
        redisTemplate.opsForValue().set(refreshToken, "blacklisted", jwtTokenProvider.getRemainingTime(refreshToken), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isTokenAllowed(String token) {
        return Boolean.FALSE.equals(redisTemplate.hasKey(token));
    }
}
