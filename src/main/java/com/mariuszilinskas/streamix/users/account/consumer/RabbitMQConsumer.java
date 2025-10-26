package com.mariuszilinskas.streamix.users.account.consumer;

import com.mariuszilinskas.streamix.users.account.dto.UserLastActiveMessage;
import com.mariuszilinskas.streamix.users.account.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final UserService userService;

    @RabbitListener(queues = "${rabbitmq.queues.verify-account}")
    public void consumeVerifyAccountMessage(UUID userId) {
        logger.info("Received message to verify account for User [userId: {}]", userId);
        userService.verifyUser(userId);
    }

    @RabbitListener(queues = "${rabbitmq.queues.update-last-active}")
    public void consumeUpdateLastActiveMessage(UserLastActiveMessage message) {
        logger.info("Received message to update lastActive for User [userId: '{}']", message.userId());
        userService.updateLastActiveInDb(message.userId(), message.lastActive());
    }

}
