package com.mokakbob.domain.member.domain;

import com.mokakbob.common.domain.BaseEntity;
import com.mokakbob.domain.member.domain.vo.MemberPreference;
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
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // 소셜 로그인 사용자는 null
    private String passwordEnc;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberPreference preference;

    // 사용자 신뢰 점수: 기본값 50
    @Column(nullable = false)
    private Integer score;

    // 가상 포인트: 기본값 2000
    @Column(nullable = false)
    private Integer depositPoint;

    public void updateProfileImage(String newProfile) {
        this.profileImage = newProfile;
    }

    public void deductPoint(int point) {
        this.depositPoint -= point;
    }
}
