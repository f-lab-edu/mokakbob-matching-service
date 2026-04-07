package com.mokakbob.auth.service;

import com.mokakbob.auth.exception.AuthApiErrorCode;
import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.domain.vo.MemberPreference;
import com.mokakbob.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @Value("${profile.default.image.url}")
    private String defaultProfileImageUrl;

    public Member login(String email, String password) {
        Member member = memberService.findMemberByEmail(email);

        if (!passwordEncoder.matches(password, member.getPasswordEnc())) {
            throw new ApiException(AuthApiErrorCode.INVALID_CREDENTIALS);
        }

        return member;
    }

    public Member signUp(String email, String password, String nickName, MemberPreference preference) {
        String passwordEnc = passwordEncoder.encode(password);

        return memberService.createMember(
                email,
                passwordEnc,
                nickName,
                defaultProfileImageUrl,
                preference
        );
    }
}
