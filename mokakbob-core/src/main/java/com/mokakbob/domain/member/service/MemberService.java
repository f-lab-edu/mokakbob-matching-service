package com.mokakbob.domain.member.service;

import com.mokakbob.domain.exception.DomainException;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.domain.vo.MemberPreference;
import com.mokakbob.domain.member.exception.MemberErrorCode;
import com.mokakbob.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final int DEFAULT_SCORE = 50;
    private static final int DEFAULT_POINT = 2000;

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Optional<Member> findByNickName(String nickName) {
        return memberRepository.findByNickname(nickName);
    }

    @Transactional(readOnly = true)
    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException(MemberErrorCode.NOT_FOUND_MEMBER_BY_EMAIL));
    }

    @Transactional
    public Member createMember(String email, String passwordEnc, String nickName, String defaultImagePath,
                               MemberPreference preference) {
        validateDuplicateEmail(email);
        validateDuplicateNickName(nickName);

        Member member = Member.builder()
                .email(email)
                .passwordEnc(passwordEnc)
                .nickname(nickName)
                .profileImage(defaultImagePath)
                .preference(preference)
                .score(DEFAULT_SCORE)
                .depositPoint(DEFAULT_POINT)
                .build();

        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new DomainException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    @Transactional
    public Member findMemberForUpdate(Long memberId) {
        return memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new DomainException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    @Transactional
    public List<Member> findMembers(List<Long> memberIds) {
        return memberRepository.findByIdIn(memberIds);
    }

    @Transactional
    public void addPoint(Long memberId, int point) {
        Member member = findMemberForUpdate(memberId);
        member.addPoint(point);
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
