package com.example.springbootproject.config;

import com.example.springbootproject.rabbitmq.RabbitConfirmCallback;
import com.example.springbootproject.rabbitmq.RabbitReturnCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

//@EnableRabbit : to enable support for Rabbit Listener.
@EnableRabbit
@Configuration
@Slf4j
public class RabbitMqConfig {

    @Bean(name = "myQueue")
    public Queue myQueue() {
        return new Queue("myQueue", true, false, false);
    }

    @Bean(name = "marketingQueue")
    public Queue marketingQueue() {

        // configure Dead letter redirection
        Map<String, Object> arguments = new HashMap<>();
//        arguments.put("x-dead-letter-exchange", "dead-letter-exchange");
        arguments.put("x-dead-letter-exchange", deadLetterExchange().getName());
        arguments.put("x-dead-letter-routing-key", "DeadLetterKey");
        arguments.put("x-message-ttl", 60000);   // 60 seconds

        return new Queue("marketingQueue", true, false, false, arguments);
    }

    @Bean(name = "financeQueue")
    public Queue financeQueue() {
        return new Queue("financeQueue", true, false, false);
    }

    @Bean(name = "adminQueue")
    public Queue adminQueue() {
        return new Queue("adminQueue", true, false, false);
    }

    @Bean(name = "deadLetterQueue")
    public Queue deadLetterQueue() {
        return new Queue("dead-letter-queue", true, false, false);
    }


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("direct-exchange", true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dead-letter-exchange", true, false);
    }

    @Bean
    @Profile("fanout")
    public FanoutExchange exchange2() {
        return new FanoutExchange("fanout-exchange", true, false);
    }

    @Bean
    @Profile("topic")
    public TopicExchange exchange3() {
        return new TopicExchange("topic-exchange", true, false);
    }

    @Bean
    public Binding marketingBinding(Queue marketingQueue, DirectExchange exchange) {
        return BindingBuilder.bind(marketingQueue)
                .to(exchange)
                .with("marketing");
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with("DeadLetterKey");
    }

    @Bean
    public Binding financeBinding(Queue financeQueue, DirectExchange exchange) {
        return BindingBuilder.bind(financeQueue)
                .to(exchange)
                .with("finance");
    }

    @Bean
    Binding adminBinding(Queue adminQueue, DirectExchange exchange) {
        return BindingBuilder.bind(adminQueue)
                .to(exchange)
                .with("admin");
    }

    @Bean
    @Profile("handled_by_spring__application.properties")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

//        https://github.com/spring-projects/spring-amqp-samples/blob/main/spring-rabbit-confirms-returns/src/main/resources/application.properties
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);

        return connectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean(value = "ackRabbitTemplate")
    public RabbitTemplate ackRabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

//        rabbitTemplate.setDefaultReceiveQueue(myQueue().getName());
//        rabbitTemplate.setUseDirectReplyToContainer(false);
//        rabbitTemplate.setReplyAddress(myQueue().getName());
//        rabbitTemplate.setReplyTimeout(6000);

        // return
        rabbitTemplate.setReturnCallback(new RabbitReturnCallback());
        // ack
        rabbitTemplate.setConfirmCallback(new RabbitConfirmCallback());

        // to handle confirmation when the message received
        rabbitTemplate.setMandatory(true);

        return rabbitTemplate;
    }

    /*
        It’s useful if we need queues to be automatically declared and bounded.
     */
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
//        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
////        factory.setConnectionFactory(connectionFactory());
////        factory.setMessageConverter(jsonMessageConverter());
//        factory.setConcurrentConsumers(2);
//        factory.setMaxConcurrentConsumers(5);
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        factory.setErrorHandler(errorHandler());
//        factory.setPrefetchCount(20);
//        return factory;
//    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(new MyFatalExceptionStrategy());
    }

    public static class MyFatalExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {
        @Override
        public boolean isFatal(Throwable t) {
            if (t instanceof ListenerExecutionFailedException) {
                ListenerExecutionFailedException lefe = (ListenerExecutionFailedException) t;
                log.error("Failed to process inbound message from queue "
                        + lefe.getFailedMessage().getMessageProperties().getConsumerQueue()
                        + "; failed message: " + lefe.getFailedMessage(), t);
            }
            return super.isFatal(t);
        }
    }

}
