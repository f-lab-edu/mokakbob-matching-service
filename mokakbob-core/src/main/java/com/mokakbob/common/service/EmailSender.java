package com.mokakbob.common.service;

public interface EmailSender {
    void sendEmail(String to, String subject, String text);
}
