package com.example.springbootproject.controller;

import com.example.springbootproject.service.RedisScriptMoneyTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RedisScriptMoneyTransferController {

    @Autowired
    private RedisScriptMoneyTransferService redisScriptMoneyTransferService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @RequestMapping(value = "/test-redis-script")
    public void run() throws Exception {

        // initialize few accounts
        redisTemplate.opsForHash().put("account", "a", "100");
        redisTemplate.opsForHash().put("account", "b",  "20");

        // transfer money with lua script
        redisScriptMoneyTransferService.transfer("a", "b", 20);

        // check the results
        log.info((String) redisTemplate.opsForHash().get("account", "a"));
        log.info((String) redisTemplate.opsForHash().get("account", "b"));
    }


}
