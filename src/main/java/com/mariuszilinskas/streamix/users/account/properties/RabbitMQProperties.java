package com.mariuszilinskas.streamix.users.account.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq")
public record RabbitMQProperties(
        String exchange,
        Queues queues,
        RoutingKeys routingKeys
) {

    public record Queues(
            String verifyAccount,
            String updateLastActive
    ) {}

    public record RoutingKeys(
            String verifyAccount,
            String profileSetup,
            String platformEmails,
            String resetPasscode,
            String updateLastActive,
            String deleteUserData
    ) {}
}
