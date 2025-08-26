package com.mokakbob.matching.listener;

import com.mokakbob.matching.service.MatchingWriterService;
import com.mokakbob.topic.KafkaTopic;
import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchingParticipateEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MatchingWriterService matchingWriterService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMatchingParticipateEvent(MatchingParticipateEvent event) {
        sendKafkaTopic(event);

        matchingWriterService.saveParticipantInfo(event);
        matchingWriterService.saveGeo(event);
        matchingWriterService.saveQueue(event);
    }

    private void sendKafkaTopic(MatchingParticipateEvent event) {
        String idempotencyKey = generateIdempotencyKey(String.valueOf(event.memberId()));

        try {
            kafkaTemplate.send(KafkaTopic.MATCHING_PARTICIPATE, idempotencyKey, event);
        } catch (Exception e) {
            log.error("카프카 토픽 발급 실패: {}\n실패한 이벤트: {}\n재처리 필요 정보 - memberId: {}, key: {}, lat: {}, lng: {}, category: {}, participantCount: {}",
                    e.getMessage(),
                    event,
                    event.memberId(),
                    idempotencyKey,
                    event.lat(),
                    event.lng(),
                    event.category(),
                    event.participantCount());
        }
    }

    private String generateIdempotencyKey(String memberId) {
        long timestamp = System.currentTimeMillis();
        return memberId + "-" + timestamp;
    }
}
