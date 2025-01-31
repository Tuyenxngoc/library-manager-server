package com.example.librarymanager.service.impl;

import com.example.librarymanager.service.EmailRateLimiterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailRateLimiterServiceImpl implements EmailRateLimiterService {

    RedisTemplate<String, String> redisTemplate;

    private String createKey(String email) {
        return "EMAIL: LIMIT: " + email.toUpperCase();
    }

    public boolean setMailLimit(String email, long timeout, TimeUnit unit) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String key = createKey(email);

        // Nếu email đã tồn tại trong Redis thì return false
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return false;
        }

        // Đặt giá trị với thời gian hết hạn
        ops.set(key, "limited", timeout, unit);
        return true;
    }

    public boolean isMailLimited(String email) {
        String key = createKey(email);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
