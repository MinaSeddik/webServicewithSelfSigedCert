package com.example.springbootproject.redis;

import com.example.springbootproject.domain.Email;
import com.example.springbootproject.domain.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisPublisherService {

    @Autowired
    private RedisTemplate<String, Email> redisTemplate;

    @Autowired
    private ChannelTopic topic;


    public void publish(Email message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

}
