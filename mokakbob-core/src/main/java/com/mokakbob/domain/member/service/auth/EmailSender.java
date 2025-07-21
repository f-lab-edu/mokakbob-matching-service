package com.mokakbob.domain.member.service.auth;

public interface EmailSender {
    void sendEmail(String to, String subject, String text);
}
