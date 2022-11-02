package com.example.springbootproject.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.UUID;

@Component
@Slf4j
public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    @Autowired
    private MyWebSocketSender myWebSocketSender;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("New session established : {}", session.getSessionId());

        session.subscribe("/topic/news", this);
        log.info("Subscribed to /topic/news");

//        session.send("/app/news", getSampleMessage());
//        log.info("Message sent to websocket server");

        myWebSocketSender.senMessages(session, "/app/news");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error("Got an exception ", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        String msg = (String) payload;
        log.info("Received : {}", msg);
    }

    /**
     * A sample message instance.
     * @return instance of <code>Message</code>
     */
    private String getSampleMessage() {
        String msg = UUID.randomUUID().toString();
        return msg;
    }
}