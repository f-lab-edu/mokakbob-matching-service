package com.mokakbob.auth.controller;

import com.mokakbob.auth.controller.request.LoginRequest;
import com.mokakbob.auth.controller.request.SignUpRequest;
import com.mokakbob.auth.controller.response.LoginResponse;
import com.mokakbob.auth.controller.response.SignUpResponse;
import com.mokakbob.auth.infrastructure.JwtTokenProvider;
import com.mokakbob.auth.service.AuthService;
import com.mokakbob.common.path.auth.AuthApiPath;
import com.mokakbob.domain.member.domain.Member;
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
    private final JwtTokenProvider tokenProvider;

    @PostMapping(AuthApiPath.LOGIN)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Member member = authService.login(request.email(), request.password());
        String token = tokenProvider.createAccessToken(member.getId());

        return ResponseEntity.ok(new LoginResponse(
                token,
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImage()
        ));
    }

    @PostMapping(AuthApiPath.SIGN_UP)
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        Member member = authService.signUp(
                request.email(),
                request.password(),
                request.nickName(),
                request.preference()
        );

        return ResponseEntity.ok(new SignUpResponse(member.getEmail(), member.getNickname()));
    }
}
