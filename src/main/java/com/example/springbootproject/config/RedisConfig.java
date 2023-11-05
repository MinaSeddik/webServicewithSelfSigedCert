package com.example.springbootproject.config;

import com.example.springbootproject.domain.Article;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class RedisConfig {


    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        // redis serialize
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);


        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
////        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
//        redisTemplate.setHashKeySerializer(jackson2JsonRedisSerializer);
//        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        // explicitly enable transaction support
//        redisTemplate.setEnableTransactionSupport(true);


//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
//        redisTemplate.setConnectionFactory(factory);

        return redisTemplate;
    }

    @Bean
    @Profile("multiple-redisTemplete")
    public RedisTemplate<String, Article> articleRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Article> articleRedisTemplate = new RedisTemplate<>();
        articleRedisTemplate.setConnectionFactory(redisConnectionFactory);

        return articleRedisTemplate;
    }

}
