package com.mokakbob.domain.matching.domain;

import com.mokakbob.common.domain.BaseEntity;
import com.mokakbob.domain.matching.domain.vo.MatchingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Matching extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String menuCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchingStatus status;
}
