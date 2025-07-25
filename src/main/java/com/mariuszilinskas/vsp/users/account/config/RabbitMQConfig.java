package com.mariuszilinskas.vsp.users.account.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queues.verify-account}")
    private String verifyAccountQueue;

    @Value("${rabbitmq.queues.update-last-active}")
    private String updateLastActiveQueue;

    @Value("${rabbitmq.routing-keys.verify-account}")
    private String verifyAccountRoutingKey;

    @Value("${rabbitmq.routing-keys.update-last-active}")
    private String updateLastActiveRoutingKey;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue verifyAccountQueue() {
        return new Queue(verifyAccountQueue, true);
    }

    @Bean
    public Queue updateLastActiveQueue() {
        return new Queue(updateLastActiveQueue, true);
    }

    @Bean
    public Binding verifyAccountBinding() {
        return BindingBuilder.bind(verifyAccountQueue())
                .to(exchange())
                .with(verifyAccountRoutingKey);
    }

    @Bean
    public Binding updateLastActiveBinding() {
        return BindingBuilder.bind(updateLastActiveQueue())
                .to(exchange())
                .with(updateLastActiveRoutingKey);
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
