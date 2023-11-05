package com.example.springbootproject.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@EnableCaching
@Configuration
public class RedisCacheConfig {


    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()));


    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(RedisConnectionFactory connectionFactory) {

        ObjectMapper objectMapper = new ObjectMapper();
//        https://docs.spring.io/spring-data-redis/reference/redis/redis-cache.html
//        fine-tune the caching behavior on a per-cache basis
        return (builder) -> builder
                .cacheWriter(RedisCacheWriter.lockingRedisCacheWriter(connectionFactory))
                .withCacheConfiguration("itemCache",
                        RedisCacheConfiguration.defaultCacheConfig()
//                                .prefixCacheNameWith("XXXXXXXX")
//                                .computePrefixWith(cacheName -> "__________" + cacheName)
                                .entryTtl(Duration.ofMinutes(10))
                                .disableCachingNullValues())

                .withCacheConfiguration("items",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper))))
                .withCacheConfiguration("item",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper))))


                .withCacheConfiguration("jwtBlacklistCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofDays(5))


                );


    }


}