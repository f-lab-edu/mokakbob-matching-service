package com.mokakbob.auth.controller;

import com.mokakbob.auth.controller.request.SignUpRequest;
import com.mokakbob.auth.controller.response.SignUpResponse;
import com.mokakbob.auth.service.AuthService;
import com.mokakbob.common.path.auth.AuthApiPath;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(AuthApiPath.SIGN_UP)
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok()
                .build();
    }
}
