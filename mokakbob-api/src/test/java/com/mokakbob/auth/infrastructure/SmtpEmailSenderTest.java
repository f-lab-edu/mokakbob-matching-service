package com.mokakbob.auth.infrastructure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
    void 이메일_전송_실패_후_recover_호출() {
        // given
        String to = "test@example.com";
        String subject = "subject";
        String text = "text";

        MailException exception = mock(MailException.class);
        doThrow(exception).when(javaMailSender).send(any(SimpleMailMessage.class));

        // when
        try {
            smtpEmailSender.sendEmail(to, subject, text);
        } catch (Exception ignored) {
        }

        // then
        verify(javaMailSender, atLeast(1))
                .send(any(SimpleMailMessage.class));
    }
}
