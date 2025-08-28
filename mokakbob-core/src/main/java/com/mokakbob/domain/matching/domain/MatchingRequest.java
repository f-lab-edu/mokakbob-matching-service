package com.mokakbob.domain.matching.domain;

import com.mokakbob.common.domain.BaseEntity;
import com.mokakbob.domain.matching.domain.vo.Location;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.domain.vo.MatchingRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
public class MatchingRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchingCategory matchingCategory;

    @Column(nullable = false)
    private Integer groupSize;

    @Embedded
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchingRequestStatus status;
}
