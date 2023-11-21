package com.example.springbootproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class RabbitMQController {

    @Autowired
    @Qualifier("ackRabbitTemplate")
//    private AmqpTemplate ackRabbitTemplate;
    private RabbitTemplate ackRabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping(value = "/rabbit-producer")
    public String fire_and_forget() throws JsonProcessingException {

        String exchange = "direct-exchange";
        String routingKey = "marketing";
        String messageData = "This is a sample message2";

        // fire and forget with not confirmation needed
//        amqpTemplate.convertAndSend(exchange, routingKey, messageData);

        // message
        MessageProperties props = MessagePropertiesBuilder.newInstance()
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setMessageId("123")
                .setHeader("bar", "baz")
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)   // to be durable
                .setPriority(5)
                .build();

        log.info("messageData: {}",  messageData);
        String payload = objectMapper.writeValueAsString(messageData);
        log.info("payload: {}",  payload);


        Message message = MessageBuilder.withBody(payload.getBytes())
                .andProperties(props)
                .build();
        ackRabbitTemplate.convertAndSend("direct-exchange", routingKey, message);


        return "Message sent to the RabbitMQ Successfully";
    }

    @GetMapping(value = "/rabbit-producer-with-confirm")
    public String send_message_with_confirmation() throws ExecutionException, InterruptedException, TimeoutException, JsonProcessingException {

        String exchange = "direct-exchange";
        String routingKey = "marketing";
        String messageData = "This is a sample message2";

        // fire and forget with not confirmation needed
//        amqpTemplate.convertAndSend(exchange, routingKey, messageData);

        // message
        MessageProperties props = MessagePropertiesBuilder.newInstance()
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setMessageId("123")
                .setHeader("bar", "baz")
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)   // to be durable
                .setPriority(5)
                .build();

        log.info("messageData: {}",  messageData);
        String payload = objectMapper.writeValueAsString(messageData);
        log.info("payload: {}",  payload);

        Message message = MessageBuilder.withBody(payload.getBytes())
                .andProperties(props)
                .build();


        // send message with confirmation
//        https://github.com/spring-projects/spring-amqp-samples/blob/main/spring-rabbit-confirms-returns/src/main/java/org/springframework/amqp/samples/confirms/SpringRabbitConfirmsReturnsApplication.java#L52
        CorrelationData correlationData = new CorrelationData("Correlation for message 1");
        ackRabbitTemplate.convertAndSend("direct-exchange", routingKey, message, correlationData);
        CorrelationData.Confirm confirm = correlationData.getFuture().get(10, TimeUnit.SECONDS);
        System.out.println("Confirm received for good delivery, ack = " + confirm.isAck());


        return "Confirm received for good delivery, ack = " + confirm.isAck();
    }
}
