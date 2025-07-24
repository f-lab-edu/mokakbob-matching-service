package com.mokakbob.auth.infrastructure;

import com.mokakbob.domain.member.repository.EmailVerifyCodeStore;
import com.mokakbob.domain.member.service.auth.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;
    private final EmailVerifyCodeStore codeStore;

    @Override
    @Async(value = "EmailExecutor")
    @Retryable(
            retryFor = {MailException.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = makeInfo(to, subject, text);

        javaMailSender.send(message);
    }

    @Recover
    public void recover(MailException e, String to, String subject, String text) {
        codeStore.deleteCode(to);
        log.error("[메일 전송 실패]: {}", e.getMessage());
    }

    private SimpleMailMessage makeInfo(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        return message;
    }
}
