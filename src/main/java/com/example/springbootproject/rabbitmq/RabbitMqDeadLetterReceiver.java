package com.example.springbootproject.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RabbitListener(queues = "dead-letter-queue", id = "dead-letter-listener")
public class RabbitMqDeadLetterReceiver {

    @RabbitHandler
    public void receiver(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("--------->>>>>>> [tag: {}] Dead-Letter Message Received : {}", tag, message);

        // some cde to analyse and log the dead letter message

        // manual Acknowledge to remove the message from the queue
        channel.basicAck(tag, false);
    }
}
