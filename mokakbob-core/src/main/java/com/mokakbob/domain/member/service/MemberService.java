package com.mokakbob.domain.member.service;

import com.mokakbob.common.exception.DomainException;
import com.mokakbob.domain.member.exception.MemberErrorCode;
import com.mokakbob.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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
