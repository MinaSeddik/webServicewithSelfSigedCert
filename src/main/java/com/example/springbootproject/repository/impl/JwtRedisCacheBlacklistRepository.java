package com.example.springbootproject.repository.impl;

import com.example.springbootproject.repository.JwtBlacklistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class JwtRedisCacheBlacklistRepository implements JwtBlacklistRepository {

    private final static String JWT_BLACKLIST = "jwtBlacklistCache";


    @Autowired
    private CacheManager cacheManager;

    @Override
    public void blacklist(String user, String token) {

        log.info("Blacklist a JWT token: {} for user: {}", token, user);
        Cache jwtBlacklistCache = cacheManager.getCache(JWT_BLACKLIST);
        jwtBlacklistCache.put(token, user);

    }

    @Override
    public boolean isTokenBlacklisted(String user, String token) {

        log.info("Find token: {} for user: {}", token, user);
        Cache jwtBlacklistCache = cacheManager.getCache(JWT_BLACKLIST);
        String username = jwtBlacklistCache.get(token, String.class);

        log.info("Token blacklisted: {}", user.equals(username));
        return user.equals(username);
    }


}
