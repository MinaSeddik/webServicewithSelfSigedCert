package com.example.springbootproject.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
@Profile("Caffeine")
public class CaffeineCacheConfig {

/*

    Reference: https://github.com/ben-manes/caffeine/discussions/511

     - expireAfterAccess and expireAfterWrite policies
     ___________________________________________________

    The two timestamps are maintained independently,
    so it is whatever has the shortest duration. expireAfterAccess will update its
    timestamp on every read or write, so it depends on which fixed duration is smaller.
    If 30s access and 5 min write, then absent of writes the access policy will evict.
    If the access duration is equal or greater, then the write policy will be dominant
    if only reads occur

 */


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


    // for rate limiting


    @Bean
    public CaffeineCache rateLimitBuckets() {

//        Could be done through configuration as well
//    spring:
//      cache:
//          cache-names:
//          - rate-limit-buckets
//          caffeine:
//              spec: maximumSize=100000,expireAfterAccess=3600s

        return new CaffeineCache("rate-limit-buckets",
                Caffeine.<String, String>newBuilder()
                        .maximumSize(100000)
                        .expireAfterAccess(3600, TimeUnit.SECONDS)
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
