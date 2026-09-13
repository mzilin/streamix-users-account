package com.mariuszilinskas.streamix.users.account.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
        int replicationFactor,
        Duration retention,
        Topics topics
) {

    public record Topics(
            Topic userRegistered,
            Topic userVerified
    ) {}

    public record Topic(
            String name,
            int partitions
    ) {}
}