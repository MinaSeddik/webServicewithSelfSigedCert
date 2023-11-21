package com.example.springbootproject.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class RabbitConfirmCallback implements RabbitTemplate.ConfirmCallback {

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        // may raise NullPointerException if correlationData is NULL
//        String messageId = correlationData.getId();

        if(ack){
            // report this message as ACKed in the database
        }

        log.info("=====================================");
        log.info("Received confirm with result {}", ack);
        log.info("=====================================");
    }

}
