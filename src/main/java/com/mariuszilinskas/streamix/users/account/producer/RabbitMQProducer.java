package com.mariuszilinskas.streamix.users.account.producer;

import com.mariuszilinskas.streamix.users.account.dto.CreateDefaultProfileMessage;
import com.mariuszilinskas.streamix.users.account.dto.UserLastActiveMessage;
import com.mariuszilinskas.streamix.users.account.properties.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties props;

    public void sendCreateDefaultProfileMessage(CreateDefaultProfileMessage message) {
        logger.info("Sending message to create default user profile: {}", message);
        rabbitTemplate.convertAndSend(props.exchange(), props.routingKeys().profileSetup(), message);
    }

    public void sendResetPasscodeMessage(UUID userId) {
        logger.info("Sending message to create user passcode: {}", userId);
        rabbitTemplate.convertAndSend(props.exchange(), props.routingKeys().resetPasscode(), userId);
    }

    public void sendUpdateLastActiveMessage(UserLastActiveMessage message) {
        logger.info("Sending message to update lastActive for User [userId: '{}']", message.userId());
        rabbitTemplate.convertAndSend(props.exchange(), props.routingKeys().updateLastActive(), message);
    }

    public void sendDeleteUserDataMessage(UUID userId) {
        logger.info("Sending message to delete user data for User [id: {}]", userId);
        rabbitTemplate.convertAndSend(props.exchange(), props.routingKeys().deleteUserData(), userId);
    }

}
