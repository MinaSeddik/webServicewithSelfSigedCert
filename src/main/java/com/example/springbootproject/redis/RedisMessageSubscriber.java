package com.example.springbootproject.redis;

import com.example.springbootproject.domain.Email;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisMessageSubscriber implements MessageListener {

    private ObjectMapper objectMapper = new ObjectMapper();
    public static List<Email> messageList = new ArrayList<>();

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Email email;
        try {
            email = objectMapper.readValue(message.toString(), Email.class);
            messageList.add(email);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Runnable runnableTask = () -> {
            try {
                // send email simulation
                TimeUnit.MILLISECONDS.sleep(300);
                log.info("Email {} sent.", email.getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        executor.execute(runnableTask);

    }
}
