package com.mokakbob.matching.config;

import com.mokakbob.topic.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    private static final int MATCHING_PARTITIONS = 1;
    private static final short MATCHING_REPLICAS = 1;
    private static final String MATCHING_RETENTION_MS = String.valueOf(600_000);

    @Bean
    public NewTopic matchingParticipateTopic() {
        return TopicBuilder.name(KafkaTopic.MATCHING_PARTICIPATE)
                .partitions(MATCHING_PARTITIONS)
                .replicas(MATCHING_REPLICAS)
                .config("retention.ms", MATCHING_RETENTION_MS)
                .build();
    }

    @Bean
    public NewTopic matchingFoundTopic() {
        return TopicBuilder.name(KafkaTopic.MATCHING_FOUND)
                .partitions(MATCHING_PARTITIONS)
                .replicas(MATCHING_REPLICAS)
                .config("retention.ms", MATCHING_RETENTION_MS)
                .build();
    }
}
