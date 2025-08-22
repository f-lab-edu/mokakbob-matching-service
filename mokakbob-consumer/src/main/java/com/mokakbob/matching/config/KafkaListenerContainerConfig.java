package com.mokakbob.matching.config;

import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaListenerContainerConfig {

    private final ConsumerFactory<String, MatchingParticipateEvent> matchingParticipateConsumerFactory;

    @Bean(name = "matchingParticipateKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MatchingParticipateEvent>
    matchingParticipateKafkaListenerContainerFactory() {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, MatchingParticipateEvent>();
        factory.setConsumerFactory(matchingParticipateConsumerFactory);
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(
                org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE
        );

        return factory;
    }
}
