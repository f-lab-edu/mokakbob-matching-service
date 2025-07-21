package com.mokakbob.domain.member.service.auth;

import com.mokakbob.domain.member.service.store.EmailVerifyCodeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final EmailVerifyCodeStore codeStore;
}
