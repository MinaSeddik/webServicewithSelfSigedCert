package com.example.springbootproject;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.testng.annotations.BeforeClass;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketTest {

    @Test
    public void testWebSocketConnection() throws InterruptedException, ExecutionException {

        log.info("test Web Socket Connection ...");

        log.info("Prepare WebSocketClient ...");
        WebSocketClient client = new StandardWebSocketClient();

        log.info("Prepare stompClient ...");
        WebSocketStompClient stompClient = new WebSocketStompClient(client);

        log.info("Set converters ...");
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setMessageConverter(new StringMessageConverter());

//        String url = "http://localhost:8080/mywebsockets";
        String url = "ws://localhost:8080/mywebsockets";

        log.info("URL = {}", url);
        StompSessionHandler sessionHandler = new MyStompSessionHandler2();
        log.info("Connecting to {}", url);
        Future<StompSession> stompSessionFuture = stompClient.connect(url, sessionHandler);

        log.info("Retrieve session from connection ...");
        StompSession session = stompSessionFuture.get();

        log.info("Sleep 5 seconds ...");
        TimeUnit.SECONDS.sleep(5);

        log.info("Send MSG for testing ...");
        session.send("/topic/news", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

//        TimeUnit.MINUTES.sleep(5);


    }
}
