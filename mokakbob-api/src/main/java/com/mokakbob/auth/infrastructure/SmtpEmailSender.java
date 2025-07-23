package com.mokakbob.auth.infrastructure;

import com.mokakbob.common.exception.DomainException;
import com.mokakbob.domain.member.service.auth.EmailSender;
import com.mokakbob.domain.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = makeInfo(to, subject, text);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new DomainException(MemberErrorCode.MAIL_EXCEPTION);
        }
    }

    private SimpleMailMessage makeInfo(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        return message;
    }
}
