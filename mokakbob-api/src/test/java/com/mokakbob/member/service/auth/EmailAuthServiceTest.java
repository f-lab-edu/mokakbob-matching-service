package com.mokakbob.member.service.auth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mokakbob.auth.domain.EmailSender;
import com.mokakbob.auth.domain.EmailVerifyCodeStore;
import com.mokakbob.auth.exception.AuthErrorCode;
import com.mokakbob.auth.service.EmailAuthService;
import com.mokakbob.common.exception.ApiException;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class EmailAuthServiceTest {

    private EmailVerifyCodeStore codeStore;
    private EmailSender emailSender;
    private EmailAuthService emailAuthService;

    @BeforeEach
    void setUp() {
        codeStore = mock(EmailVerifyCodeStore.class);
        emailSender = mock(EmailSender.class);
        emailAuthService = new EmailAuthService(codeStore, emailSender);
    }

    @Test
    void 인증_코드_이메일_전송_성공() {
        // given
        String email = "test@example.com";
        when(codeStore.hasCode(email)).thenReturn(false);

        // when
        emailAuthService.sendEmail(email);

        // then
        verify(codeStore).saveCode(eq(email), anyString(), eq(Duration.ofMinutes(5)));
        verify(emailSender).sendEmail(eq(email), anyString(), contains("인증 코드"));
    }

    @Test
    void 쿨다운_중복_요청_시_예외_발생() {
        // given
        String email = "cooldown@example.com";
        when(codeStore.hasCode(email)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> emailAuthService.sendEmail(email))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(AuthErrorCode.TOO_MANY_REQUEST.getMessage());
    }
}
