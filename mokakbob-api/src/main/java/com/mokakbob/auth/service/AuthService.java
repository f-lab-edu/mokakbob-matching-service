package com.mokakbob.auth.service;

import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.domain.vo.MemberPreference;
import com.mokakbob.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public Member signUp(String email, String password, String nickName, MemberPreference preference) {
        String passwordEnc = passwordEncoder.encode(password);

        return memberService.createMember(
                email,
                passwordEnc,
                nickName,
                preference
        );
    }
}
