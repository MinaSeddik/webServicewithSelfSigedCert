package com.example.springbootproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RedisScriptMoneyTransferService {

    @Autowired
    private RedisScript<Boolean> script;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void transfer(String fromAccount, String toAccount, int amount){
        log.info("fromAccount: {}", fromAccount);
        log.info("toAccount: {}", toAccount);
        log.info("amount: {}", amount);

        redisTemplate.execute(script, List.of(fromAccount, toAccount), String.valueOf(amount));
    }


}
