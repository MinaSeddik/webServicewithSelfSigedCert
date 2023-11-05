package com.example.springbootproject.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RedisPipelineService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


//    Redis pipeline is NOT Atomic, it just save multiple round-trip to the server to enhance performance
    public List<Object> executePipeline(String value){
        log.info("executePipeline ...");

        List<Object> results = redisTemplate.executePipelined(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                for(int i = 0; i < 5; i++) {
                    connection.lPush("pipeline-list".getBytes(), value.getBytes());
                    connection.rPop("pipeline-list".getBytes());
                }
                return null;
            }
        });

        log.info("Results: {}", results);
        return results;
    }


    public List<Object> executeTransaction(List<Integer> numbers) {

        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {

            @Override
            public List<Object> execute(@NonNull RedisOperations operations) throws DataAccessException {

                operations.multi();

                try {
                    StringBuilder redisValue = new StringBuilder("Saved numbers");
                    for (Integer number : numbers) {
                        log.info("Processed number is " + number);
                        if (number == 5) {
                            throw new IllegalArgumentException("Bad number");
                        }
                        redisValue.append("___").append(number);
                        operations.opsForValue().set("numbers", redisValue.toString());
                    }
                } catch (Exception e) {
                    log.error("Exception occurred: {}", e.getMessage());
                    operations.discard();
                    return null;
                }

                return operations.exec();
            }
        });
        if (txResults != null) {
            log.info("Redis process count: {}", txResults.size());
        }

        return txResults;
    }
}
