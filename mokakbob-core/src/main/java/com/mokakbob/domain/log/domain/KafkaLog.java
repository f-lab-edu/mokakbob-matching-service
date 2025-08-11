package com.mokakbob.domain.log.domain;

import com.mokakbob.common.domain.BaseEntity;
import com.mokakbob.domain.log.domain.vo.KafkaLogStatus;
import com.mokakbob.domain.log.domain.vo.KafkaTopic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class KafkaLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KafkaTopic topic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String eventData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KafkaLogStatus status;
}
