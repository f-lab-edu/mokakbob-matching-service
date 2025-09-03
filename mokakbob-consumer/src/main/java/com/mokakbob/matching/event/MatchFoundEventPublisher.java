package com.mokakbob.matching.event;

import com.mokakbob.domain.matching.event.MatchingFoundEvent;
import com.mokakbob.topic.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchFoundEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishFound(MatchingFoundEvent event) {
        try {
            kafkaTemplate.send(KafkaTopic.MATCHING_FOUND, event.key(), event);
            log.info("카프카 토픽 발급 성공: topic={}, key={}, matched={}, category={}, participantCount={}",
                    KafkaTopic.MATCHING_FOUND,
                    event.key(),
                    event.matched(),
                    event.category(),
                    event.participantCount());
        } catch (Exception e) {
            log.error("카프카 토픽 발급 실패: {}\n실패한 이벤트: {}\n재처리 필요 정보 - key: {}, category: {}, participantCount: {}, matched: {}",
                    e.getMessage(),
                    event,
                    event.key(),
                    event.category(),
                    event.participantCount(),
                    event.matched());
        }
    }
}
