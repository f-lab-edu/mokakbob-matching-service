package com.mokakbob.domain.member.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.mokakbob.common.exception.DomainException;
import com.mokakbob.domain.member.exception.MemberErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SuppressWarnings("NonAsciiCharacters")
class SmtpEmailSenderTest {

    private final JavaMailSender javaMailSender = mock(JavaMailSender.class);
    private final SmtpEmailSender smtpEmailSender = new SmtpEmailSender(javaMailSender);

    @Test
    void 이메일_전송_성공() {
        // given
        String to = "test@example.com";
        String subject = "Subject";
        String text = "text";

        // when
        smtpEmailSender.sendEmail(to, subject, text);

        // then
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void 이메일_전송_실패() {
        // given
        String to = "test@example.com";
        String subject = "Subject";
        String text = "Hello";

        doThrow(mock(MailException.class))
                .when(javaMailSender)
                .send(any(SimpleMailMessage.class));

        // when & then
        assertThatThrownBy(() -> smtpEmailSender.sendEmail(to, subject, text))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(MemberErrorCode.MAIL_EXCEPTION.message());
    }
}
