package com.example.springbootproject.config;

import com.example.springbootproject.domain.Email;
import com.example.springbootproject.domain.Payment;
import com.example.springbootproject.redis.RedisMessageSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisPubSubConfig {

    @Bean
    public ChannelTopic topic() {
        // channel name or (Queue name)
        return new ChannelTopic("queue:email");
    }

    @Bean
    public RedisTemplate<String, Email> paymentRedisTemplate(RedisConnectionFactory factory) {
        final RedisTemplate<String, Email> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Email.class));

        return template;
    }


    @Autowired
    private RedisMessageSubscriber redisMessageSubscriber;

    @Bean
    public MessageListenerAdapter messageListener( ) {
        return new MessageListenerAdapter(redisMessageSubscriber);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(messageListener(), topic());
        return container;
    }
}
