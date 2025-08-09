package com.mokakbob.domain.outbox.service;

import com.mokakbob.domain.outbox.domain.Outbox;
import com.mokakbob.domain.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    @Transactional
    public void saveOutbox(Long aggregateId, String eventType, String payload, String topic) {
        Outbox outbox = Outbox.builder()
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .topic(topic)
                .build();

        outboxRepository.save(outbox);
    }
}
