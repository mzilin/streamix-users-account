package com.mariuszilinskas.streamix.users.account.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
        Topics topics
) {

    public record Topics(
            String userRegistered,
            String userVerified
    ) {}
}