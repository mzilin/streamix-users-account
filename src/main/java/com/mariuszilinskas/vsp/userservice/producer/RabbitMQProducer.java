package com.mariuszilinskas.vsp.userservice.producer;

import com.mariuszilinskas.vsp.userservice.dto.CreateUserDefaultProfileRequest;
import com.mariuszilinskas.vsp.userservice.dto.CredentialsRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-keys.create-credentials}")
    private String createCredentialsRoutingKey;

    @Value("${rabbitmq.routing-keys.profile-setup}")
    private String profileSetupRoutingKey;

    @Value("${rabbitmq.routing-keys.reset-passcode}")
    private String resetPasscodeRoutingKey;

    @Value("${rabbitmq.routing-keys.delete-user-data}")
    private String deleteUserDataRoutingKey;

    public void sendCreateCredentialsMessage(CredentialsRequest request) {
        logger.info("Sending message to create user credentials: {}", request);
        rabbitTemplate.convertAndSend(exchange, createCredentialsRoutingKey, request);
    }

    public void sendCreateUserDefaultProfileMessage(CreateUserDefaultProfileRequest request) {
        logger.info("Sending message to create default user profile: {}", request);
        rabbitTemplate.convertAndSend(exchange, profileSetupRoutingKey, request);
    }

    public void sendResetPasscodeMessage(UUID userId) {
        logger.info("Sending message to create user passcode: {}", userId);
        rabbitTemplate.convertAndSend(exchange, resetPasscodeRoutingKey, userId);
    }

    public void sendDeleteUserDataMessage(UUID userId) {
        logger.info("Sending message to delete user data for User [id: {}]", userId);
        rabbitTemplate.convertAndSend(exchange, deleteUserDataRoutingKey, userId);
    }

}
