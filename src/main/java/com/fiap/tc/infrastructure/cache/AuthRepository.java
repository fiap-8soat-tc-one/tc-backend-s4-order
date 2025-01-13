package com.fiap.tc.infrastructure.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class AuthRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final Integer ttlInMilliseconds;

    public AuthRepository(RedisTemplate<String, String> redisTemplate,
                          @Value("${app.auth.backoffice.tokenExpiration}") Integer ttlInMilliseconds) {
        this.redisTemplate = redisTemplate;
        this.ttlInMilliseconds = ttlInMilliseconds;
    }

    public Optional<String> getToken(String userName) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(userName));
    }

    public void saveToken(String userName, String token) {
        redisTemplate.opsForValue().set(userName, token, Duration.ofMillis(ttlInMilliseconds));
    }
}
