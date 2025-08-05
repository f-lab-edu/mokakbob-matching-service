package com.mokakbob.auth.controller;

import com.mokakbob.auth.controller.request.EmailCodeRequest;
import com.mokakbob.auth.service.EmailAuthService;
import com.mokakbob.auth.controller.request.EmailSendRequest;
import com.mokakbob.common.path.auth.AuthEmailPath;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthEmailController {

    private final EmailAuthService emailAuthService;

    @PostMapping(AuthEmailPath.SEND)
    public ResponseEntity<Void> sendEmailVerificationCode(@Valid @RequestBody EmailSendRequest request) {
        emailAuthService.sendEmail(request.email());
        return ResponseEntity.ok()
                .build();
    }

    @PostMapping(AuthEmailPath.VERIFY)
    public ResponseEntity<Void> verifyEmailCode(@Valid @RequestBody EmailCodeRequest request) {
        emailAuthService.checkEmailCode(request.email(), request.code());
        return ResponseEntity.ok()
                .build();
    }
}
