package com.mokakbob.member.controller.auth;

import com.mokakbob.domain.member.service.auth.EmailAuthService;
import com.mokakbob.member.controller.auth.request.EmailSendRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthEmailController {

    private final EmailAuthService emailAuthService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendEmailVerificationCode(@Valid @RequestBody EmailSendRequest request) {
        emailAuthService.sendEmail(request.email());
        return ResponseEntity.ok()
                .build();
    }
}
