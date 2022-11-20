package com.example.springbootproject.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CaffeineCacheConfig {


    @Value("${jwt.refreshToken.timeout}")
    private Duration tokenDuration;

    // Just expose your custom caches as beans.
    // They are automatically added to the CaffeineCacheManager.
    @Bean
    public CaffeineCache addressCache() {
        return new CaffeineCache("addresses",
                Caffeine.newBuilder()
                        .initialCapacity(100)
                        .maximumSize(500)
                        .expireAfterAccess(10, TimeUnit.MINUTES)
                        .weakKeys()
                        .recordStats()
                        .build());
    }

    @Bean
    public CaffeineCache jwtBlacklistCache() {

        long timeoutInDays = TimeUnit.DAYS.convert(tokenDuration);

        return new CaffeineCache("jwtBlacklistCache",
                Caffeine.<String, String>newBuilder()
                        .expireAfterWrite(timeoutInDays, TimeUnit.DAYS)
                        .recordStats()
                        .build());
    }


//    @Bean
//    public Caffeine caffeine() {
//
//        return Caffeine.newBuilder()
//                .initialCapacity(100)
//                .maximumSize(500)
//                .expireAfterAccess(10, TimeUnit.MINUTES)
//                .weakKeys()
//                .recordStats();
//    }

//    @Bean
//    @Profile("dev")
//    public CacheManager getNoOpCacheManager() {
//        return new NoOpCacheManager();
//    }

//    @Bean
////    @Profile("!dev")
//    public CacheManager cacheManager(Caffeine caffeine) {
//        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
//
//        // we can remove this line
//        Cache addressCache = caffeineCacheManager.getCache("addresses");
//
//        caffeineCacheManager.setCaffeine(caffeine);
//        return caffeineCacheManager;
//    }


}
