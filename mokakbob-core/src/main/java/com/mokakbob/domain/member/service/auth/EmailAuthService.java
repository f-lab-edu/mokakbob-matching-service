package com.mokakbob.domain.member.service.auth;

import com.mokakbob.common.exception.DomainException;
import com.mokakbob.common.service.EmailSender;
import com.mokakbob.domain.member.exception.MemberErrorCode;
import com.mokakbob.domain.member.repository.EmailVerifyCodeStore;
import com.mokakbob.domain.member.util.RandomNumberGenerator;
import java.time.Duration;
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
        String code = RandomNumberGenerator.generate(CODE_LENGTH);

        Duration expiration = Duration.ofMinutes(CODE_EXPIRATION_MINUTES);
        codeStore.saveCode(email, code, expiration);

        String text = String.format("인증 코드: %s\n이 코드는 %d분 후에 만료됩니다.", code, CODE_EXPIRATION_MINUTES);

        try {
            emailSender.sendEmail(email, MAIL_SUBJECT, text);
        } catch (Exception e) {
            codeStore.deleteCode(email);
        }
    }

    public boolean verifyEmailCode(String email, String code) {
        String storedCode = codeStore.getCode(email)
                .orElseThrow(() -> new DomainException(MemberErrorCode.NOT_FOUND_MAIL_CODE));

        if (!storedCode.equals(code)) {
            throw new DomainException(MemberErrorCode.NOT_MATCH_MAIL_CODE);
        }
        codeStore.deleteCode(email);

        return true;
    }
}
