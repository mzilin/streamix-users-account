package com.mariuszilinskas.streamix.users.account.config;

import com.mariuszilinskas.streamix.users.account.config.properties.KafkaProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public NewTopic userRegisteredTopic() {
        return createTopic(kafkaProperties.topics().userRegistered());
    }

    @Bean
    public NewTopic userVerifiedTopic() {
        return createTopic(kafkaProperties.topics().userVerified());
    }

    private NewTopic createTopic(KafkaProperties.Topic topic) {
        return TopicBuilder.name(topic.name())
                .partitions(topic.partitions())
                .replicas(kafkaProperties.replicationFactor())
                .config(
                        TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(kafkaProperties.retention().toMillis())
                )
                .build();
    }
}