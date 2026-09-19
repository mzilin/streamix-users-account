package com.mariuszilinskas.streamix.users.account.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
        Topics topics
) {

    public record Topics(
            Topic userRegistered,
            Topic userVerified
    ) {}

    public record Topic(
            String name,
            int partitions,
            int replicationFactor,
            Duration retention
    ) {}
}