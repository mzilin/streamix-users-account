package com.mariuszilinskas.streamix.users.account.config;

import com.mariuszilinskas.streamix.users.account.properties.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final RabbitMQProperties props;

    public RabbitMQConfig(RabbitMQProperties props) {
        this.props = props;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(props.exchange());
    }

    @Bean
    public Queue verifyAccountQueue() {
        return new Queue(props.queues().verifyAccount(), true);
    }

    @Bean
    public Queue updateLastActiveQueue() {
        return new Queue(props.queues().updateLastActive(), true);
    }

    @Bean
    public Binding verifyAccountBinding() {
        return BindingBuilder.bind(verifyAccountQueue())
                .to(exchange())
                .with(props.routingKeys().verifyAccount());
    }

    @Bean
    public Binding updateLastActiveBinding() {
        return BindingBuilder.bind(updateLastActiveQueue())
                .to(exchange())
                .with(props.routingKeys().updateLastActive());
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
