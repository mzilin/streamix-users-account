package com.mariuszilinskas.vsp.userservice.consumer;

import com.mariuszilinskas.vsp.userservice.service.UserService;
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
        logger.info("Received request to verify account for User [userId: {}]", userId);
        userService.verifyUser(userId);
    }

}
