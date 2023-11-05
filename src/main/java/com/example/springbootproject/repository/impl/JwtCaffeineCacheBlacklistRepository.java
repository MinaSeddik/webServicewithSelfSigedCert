package com.example.springbootproject.repository.impl;

import com.example.springbootproject.repository.JwtBlacklistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("jwt-caffeine")
@Repository
@Slf4j
public class JwtCaffeineCacheBlacklistRepository implements JwtBlacklistRepository {

//    private final static String JWT_BLACKLIST = "jwtBlacklistCache";

    @Autowired
    private Cache jwtBlacklistCache;

    @Override
    public void blacklist(String user, String token) {

        log.info("Blacklist a JWT token: {} for user: {}", token, user);
//        Cache jwtBlacklistCache = cacheManager.getCache(JWT_BLACKLIST);
        jwtBlacklistCache.put(token, user);

    }

    @Override
    public boolean isTokenBlacklisted(String user, String token) {

        log.info("Find token: {} for user: {}", token, user);
//        Cache jwtBlacklistCache = cacheManager.getCache(JWT_BLACKLIST);
        String username = jwtBlacklistCache.get(token, String.class);

        log.info("Token blacklisted: {}", user.equals(username));
        return user.equals(username);
    }


}

