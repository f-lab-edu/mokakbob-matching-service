package com.mokakbob.admin.service;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.matching.service.event.MatchingParticipateEvent;
import com.mokakbob.topic.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminKafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void resendParticipateTopic(String key, Long memberId, double lat, double lng, MatchingCategory category, int participantCount) {
        MatchingParticipateEvent event = new MatchingParticipateEvent(
              memberId, lat, lng, category, participantCount
        );

        try {
            kafkaTemplate.send(KafkaTopic.MATCHING_PARTICIPATE.getTopicName(), key, event);
        } catch (Exception e) {
            log.error("카프카 토픽 재발급 실패: {}\n실패한 이벤트: {}\n재처리 필요 정보 - memberId: {}, key: {}, lat: {}, lng: {}, category: {}, participantCount: {}",
                    e.getMessage(),
                    event,
                    event.memberId(),
                    event.memberId(),
                    event.lat(),
                    event.lng(),
                    event.category(),
                    event.participantCount());
        }
    }
}
