package com.mariuszilinskas.streamix.users.account.producer;

import com.mariuszilinskas.streamix.users.account.dto.CreateDefaultProfileMessage;
import com.mariuszilinskas.streamix.users.account.dto.UserLastActiveMessage;
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

    @Value("${rabbitmq.routing-keys.profile-setup}")
    private String profileSetupRoutingKey;

    @Value("${rabbitmq.routing-keys.reset-passcode}")
    private String resetPasscodeRoutingKey;

    @Value("${rabbitmq.routing-keys.update-last-active}")
    private String updateLastActiveRoutingKey;

    @Value("${rabbitmq.routing-keys.delete-user-data}")
    private String deleteUserDataRoutingKey;

    public void sendCreateDefaultProfileMessage(CreateDefaultProfileMessage message) {
        logger.info("Sending message to create default user profile: {}", message);
        rabbitTemplate.convertAndSend(exchange, profileSetupRoutingKey, message);
    }

    public void sendResetPasscodeMessage(UUID userId) {
        logger.info("Sending message to create user passcode: {}", userId);
        rabbitTemplate.convertAndSend(exchange, resetPasscodeRoutingKey, userId);
    }

    public void sendUpdateLastActiveMessage(UserLastActiveMessage message) {
        logger.info("Sending message to update lastActive for User [userId: '{}']", message.userId());
        rabbitTemplate.convertAndSend(exchange, updateLastActiveRoutingKey, message);
    }

    public void sendDeleteUserDataMessage(UUID userId) {
        logger.info("Sending message to delete user data for User [id: {}]", userId);
        rabbitTemplate.convertAndSend(exchange, deleteUserDataRoutingKey, userId);
    }

}
