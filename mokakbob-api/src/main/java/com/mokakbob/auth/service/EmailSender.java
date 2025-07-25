package com.mokakbob.auth.service;

public interface EmailSender {
    void sendEmail(String to, String subject, String text);
}
