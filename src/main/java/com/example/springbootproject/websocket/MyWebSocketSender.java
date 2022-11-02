package com.example.springbootproject.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MyWebSocketSender {

    @Async
    public void senMessages(StompSession session, String destination){

        while (true){

            String msg = UUID.randomUUID().toString();
            log.info("Send message: {}", msg);
            session.send(destination, msg);

            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            log.info("-------------------------------------");
        }
    }
}
