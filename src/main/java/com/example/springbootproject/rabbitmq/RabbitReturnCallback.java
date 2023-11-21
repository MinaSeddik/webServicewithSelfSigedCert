package com.example.springbootproject.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class RabbitReturnCallback implements RabbitTemplate.ReturnCallback {
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText,
                                String exchange, String routingKey) {

        log.info("Received returnedMessage with result {}", routingKey);

    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.info("Received returnedMessage with ReturnedMessage {}", returned);
        RabbitTemplate.ReturnCallback.super.returnedMessage(returned);

        log.info("Returned: " + returned.getMessage() + "\nreplyCode: " + returned.getReplyCode()
                + "\nreplyText: " + returned.getReplyText() + "\nexchange/rk: "
                + returned.getExchange() + "/" + returned.getRoutingKey());

    }
}
