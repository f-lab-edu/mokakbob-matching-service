package com.mokakbob.domain.point.domain;

import com.mokakbob.common.domain.BaseEntity;
import com.mokakbob.domain.point.domain.vo.PayStatus;
import com.mokakbob.domain.point.domain.vo.PayType;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayType payType;

    @Column(nullable = false, unique = true)
    private String impUid;

    @Column(nullable = false, unique = true)
    private String merchantUid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayStatus status;
}
