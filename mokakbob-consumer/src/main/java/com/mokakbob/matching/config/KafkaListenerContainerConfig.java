package com.mokakbob.matching.config;

import com.mokakbob.domain.matching.event.MatchingFoundEvent;
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
    private final ConsumerFactory<String, MatchingFoundEvent> matchingFoundConsumerFactory;

    @Bean(name = "matchingParticipateKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MatchingParticipateEvent>
    matchingParticipateKafkaListenerContainerFactory() {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, MatchingParticipateEvent>();
        factory.setConsumerFactory(matchingParticipateConsumerFactory);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(
                org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE
        );

        return factory;
    }

    @Bean(name = "matchingFoundKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MatchingFoundEvent>
    matchingFoundKafkaListenerContainerFactory() {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, MatchingFoundEvent>();
        factory.setConsumerFactory(matchingFoundConsumerFactory);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(
                org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE
        );

        return factory;
    }
}
