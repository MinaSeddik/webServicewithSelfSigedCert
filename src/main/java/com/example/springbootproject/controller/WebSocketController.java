package com.example.springbootproject.controller;


import lombok.extern.slf4j.Slf4j;
//import org.apache.tomcat.websocket.Constants;
//import org.apache.tomcat.websocket.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.net.ssl.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private StompSessionHandler sessionHandler;

//    @MessageMapping("/news")
//    @SendTo("/topic/news")
//    public void broadcastNews(@Payload String message) {
//
//        return message;
//    }

    @MessageMapping("/news")   // STOMP SEND command to /app/news
    @SendTo("/topic/news")
    public String broadcastNews2(@Payload String message) {

        log.info("Message received: {}", message);

        // used as replacement of @SendTo
//        simpMessagingTemplate.convertAndSend("/topic/news", message);

        return message;
    }

    /*
        @MessageMapping-annotated methods will react only to the SEND messages with the destination having prefix /app and matching the topic set in the annotation.
        @SubscribeMapping-annotated methods will react only to the SUBSCRIBE messages with the destination matching the topic set in the annotation.
     */
    // please note that, this method, will not be called, however it subscribes the caller to the specified topic
    @SubscribeMapping("/topic/news")    // STOMP SUBSCRIBE to /topic/news
    public String subscribe() {

        log.info("Subscribed to /topic/news");

        String message = UUID.randomUUID().toString();
//        simpMessagingTemplate.convertAndSend("/topic/news", message);
        return message;
    }


    @RequestMapping(value = "/senddata")
    public String testsend() {

        log.info("sending data ...");

        String message = UUID.randomUUID().toString();
        simpMessagingTemplate.convertAndSend("/topic/news", message);

        return message;
    }


    @RequestMapping(value = "/test-web-socket")
    public void testsendws() throws InterruptedException {

        log.info("test ws...");

        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setMessageConverter(new StringMessageConverter());

//        String url = "http://localhost:8080/mywebsockets";
        String url = "ws://localhost:8080/mywebsockets";

        log.info("Connecting to {}", url);
        stompClient.connect(url, sessionHandler);

        TimeUnit.MINUTES.sleep(5);


    }

    @RequestMapping(value = "/test-web-socket-secure")
    public void testsendwsSecured() throws Exception {

        log.info("test wss secure...");


        StandardWebSocketClient client = new StandardWebSocketClient();

        Map<String, Object> userProperties = new HashMap<>();

//        JdkSslClientContext sslContext = getSslContext();

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        HostnameVerifier allHostsValid = (hostname, session) -> true;

//        userProperties.put(Constants.SSL_CONTEXT_PROPERTY, sslContext);
//        userProperties.put(Constants.XXXXX, allHostsValid);
        client.setUserProperties(userProperties);

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setMessageConverter(new StringMessageConverter());

//        String url = "https://localhost:8443/mywebsockets";
        String url = "wss://localhost:8443/mywebsockets";

        log.info("Connecting to {}", url);
        ListenableFuture<StompSession> sessionListenableFuture = stompClient.connect(url, sessionHandler);

        StompSession session = sessionListenableFuture.get();

        TimeUnit.MINUTES.sleep(5);


    }


}
