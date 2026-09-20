package com.mariuszilinskas.streamix.users.account.config;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jacksonConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer(MessageConverter jacksonConverter) {
        return rabbitTemplate -> rabbitTemplate.setMessageConverter(jacksonConverter);
    }

}
