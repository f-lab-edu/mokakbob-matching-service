package com.mokakbob.auth.domain;

public interface EmailSender {
    void sendEmail(String to, String subject, String text);
}
