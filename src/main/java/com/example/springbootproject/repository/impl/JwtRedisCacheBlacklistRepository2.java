package com.example.springbootproject.repository.impl;

import com.example.springbootproject.repository.JwtBlacklistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class JwtRedisCacheBlacklistRepository2 implements JwtBlacklistRepository {

    private final static String JWT_BLACKLIST_KEY_PREFIX = "jwtBlacklist";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void blacklist(String user, String token) {

        log.info("Blacklist a JWT token: {} for user: {}", token, user);
//        Cache jwtBlacklistCache = cacheManager.getCache(JWT_BLACKLIST);
//        jwtBlacklistCache.put(token, user);

        String key = JWT_BLACKLIST_KEY_PREFIX + ":" + token;
        Duration duration = Duration.ofMinutes(60);
        redisTemplate.opsForValue().set(key, user, duration);

    }

    @Override
    public boolean isTokenBlacklisted(String user, String token) {

        log.info("Find token: {} for user: {}", token, user);
//        Cache jwtBlacklistCache = cacheManager.getCache(JWT_BLACKLIST);
//        String username = jwtBlacklistCache.get(token, String.class);

        String key = JWT_BLACKLIST_KEY_PREFIX + ":" + token;
        String username = redisTemplate.opsForValue().get(key);


        log.info("Token blacklisted: {}", user.equals(username));
        return user.equals(username);
    }


}
