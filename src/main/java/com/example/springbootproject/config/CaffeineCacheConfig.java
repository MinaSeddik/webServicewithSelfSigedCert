package com.example.springbootproject.config;

import com.example.springbootproject.model.BankAccount;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CaffeineCacheConfig {

    @Bean
    public Caffeine caffeine() {
//        return Caffeine.newBuilder()
//                .expireAfterWrite(60, TimeUnit.MINUTES);

        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .weakKeys()
                .recordStats();
    }

    @Bean
    @Profile("dev")
    public CacheManager getNoOpCacheManager() {
        return new NoOpCacheManager();
    }

    @Bean
//    @Profile("!dev")
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();

        // we can remove this line
        Cache addressCache = caffeineCacheManager.getCache("addresses");

        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }


}
