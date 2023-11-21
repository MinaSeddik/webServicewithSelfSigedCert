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
//@RabbitListener(queues = "${eventqueue}")
//@RabbitListener(queues = "marketingQueue", id = "listener", concurrency = "4")
@RabbitListener(queues = "marketingQueue", id = "listener")
public class RabbitMqReceiver {

    @RabbitHandler
    public void receiver(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("--------->>>>>>> [tag: {}] Message Received : {}", tag, message);


        try {

            // code for processing the message

        } catch (Exception ex) {
            // handle exception

            // should reject the message and if there is a dead-letter queue, it will be moved to it
            channel.basicReject(tag, false);
//            channel.basicReject(tag, true);
//            channel.basicNack(tag, false,false);
        }

        // manual Acknowledge if everything went good
        channel.basicAck(tag, false);

    }

}