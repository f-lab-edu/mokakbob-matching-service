package com.mokakbob.auth.service;

import com.mokakbob.auth.domain.EmailSender;
import com.mokakbob.auth.exception.AuthErrorCode;
import com.mokakbob.common.exception.ApiException;
import com.mokakbob.auth.domain.EmailVerifyCodeStore;
import com.mokakbob.auth.util.RandomNumberGenerator;
import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private static final int CODE_LENGTH = 6;
    private static final long CODE_EXPIRATION_MINUTES = 5;
    private static final String MAIL_SUBJECT = "[모각밥] 인증 코드";

    private final EmailVerifyCodeStore codeStore;
    private final EmailSender emailSender;

    public void sendEmail(String email) {
        checkCoolDown(email);
        String code = RandomNumberGenerator.generate(CODE_LENGTH);

        Duration expiration = Duration.ofMinutes(CODE_EXPIRATION_MINUTES);
        codeStore.saveCode(email, code, expiration);
        String text = String.format("인증 코드: %s\n이 코드는 %d분 후에 만료됩니다.", code, CODE_EXPIRATION_MINUTES);

        emailSender.sendEmail(email, MAIL_SUBJECT, text);
    }

    public void checkEmailCode(String email, String code) {
        String redisMemberCode = codeStore.getCode(email)
                .orElseThrow(() -> new ApiException(AuthErrorCode.NOT_EXIST_MAIL_CODE));

        if (!Objects.equals(redisMemberCode, code)) {
            throw new ApiException(AuthErrorCode.NOT_MATCH_MAIL_CODE);
        }

        codeStore.deleteCode(email);
    }

    private void checkCoolDown(String email) {
        if (codeStore.hasCode(email)) {
            throw new ApiException(AuthErrorCode.TOO_MANY_REQUEST);
        }
    }
}
