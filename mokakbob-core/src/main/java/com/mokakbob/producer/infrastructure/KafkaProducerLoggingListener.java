package com.mokakbob.producer.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProducerLoggingListener implements ProducerListener<String, Object> {

    @Override
    public void onSuccess(ProducerRecord<String, Object> record, RecordMetadata metadata) {
        log.info("Kafka 메시지 전송 성공 - Topic: {}, Partition: {}, Offset: {}",
                metadata.topic(), metadata.partition(), metadata.offset());
    }

    @Override
    public void onError(ProducerRecord<String, Object> record, RecordMetadata metadata, Exception exception) {
        log.error("Kafka 메시지 전송 실패", exception);
    }
}
