package com.mokakbob.domain.member.service;

import com.mokakbob.common.exception.DomainException;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.domain.vo.MemberPreference;
import com.mokakbob.domain.member.exception.MemberErrorCode;
import com.mokakbob.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final int DEFAULT_SCORE = 50;
    private static final int DEFAULT_POINT = 2000;

    private final MemberRepository memberRepository;

    @Transactional
    public Member createMember(String email, String passwordEnc, String nickName, MemberPreference preference) {
        validateDuplicateEmail(email);
        validateDuplicateNickName(nickName);

        Member member = Member.builder()
                .email(email)
                .passwordEnc(passwordEnc)
                .nickname(nickName)
                .preference(preference)
                .score(DEFAULT_SCORE)
                .depositPoint(DEFAULT_POINT)
                .build();

        return memberRepository.save(member);
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DomainException(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateDuplicateNickName(String nickName) {
        if (memberRepository.existsByNickname(nickName)) {
            throw new DomainException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
