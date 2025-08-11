package com.mokakbob.domain.log.service;

import com.mokakbob.domain.log.domain.KafkaLog;
import com.mokakbob.domain.log.domain.vo.KafkaLogStatus;
import com.mokakbob.domain.log.domain.vo.KafkaTopic;
import com.mokakbob.domain.log.repository.KafkaLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KafkaLogService {

    private final KafkaLogRepository repository;

    @Transactional
    public void saveLog(String eventKey, KafkaTopic topic, String data, KafkaLogStatus status) {
        KafkaLog log = KafkaLog.builder()
                .eventKey(eventKey)
                .topic(topic)
                .eventData(data)
                .status(status)
                .build();

        repository.save(log);
    }
}
