package com.mokakbob.matching.config;

import com.mokakbob.domain.matching.event.MatchingFoundEvent;
import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, MatchingParticipateEvent> matchingParticipateConsumerFactory() {
        return buildConsumerFactory(MatchingParticipateEvent.class, "matching-participate-consumers");
    }

    @Bean
    public ConsumerFactory<String, MatchingFoundEvent> matchingFoundConsumerFactory() {
        return buildConsumerFactory(MatchingFoundEvent.class, "matching-found-consumers");
    }

    private <T> ConsumerFactory<String, T> buildConsumerFactory(Class<T> targetType, String groupId) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(targetType)
        );
    }
}
