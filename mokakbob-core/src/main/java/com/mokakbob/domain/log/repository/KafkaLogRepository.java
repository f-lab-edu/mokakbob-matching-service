package com.mokakbob.domain.log.repository;

import com.mokakbob.domain.log.domain.KafkaLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KafkaLogRepository extends JpaRepository<KafkaLog, Long> {
}
