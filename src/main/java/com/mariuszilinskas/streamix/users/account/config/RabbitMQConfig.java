package com.mariuszilinskas.streamix.users.account.config;

import com.mariuszilinskas.streamix.users.account.properties.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(rabbitMQProperties.exchange());
    }

    @Bean
    public Queue verifyAccountQueue() {
        return new Queue(rabbitMQProperties.queues().verifyAccount(), true);
    }

    @Bean
    public Queue updateLastActiveQueue() {
        return new Queue(rabbitMQProperties.queues().updateLastActive(), true);
    }

    @Bean
    public Binding verifyAccountBinding() {
        return BindingBuilder.bind(verifyAccountQueue())
                .to(exchange())
                .with(rabbitMQProperties.routingKeys().verifyAccount());
    }

    @Bean
    public Binding updateLastActiveBinding() {
        return BindingBuilder.bind(updateLastActiveQueue())
                .to(exchange())
                .with(rabbitMQProperties.routingKeys().updateLastActive());
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jacksonConverter() {
        return new JacksonJsonMessageConverter();
    }

}
