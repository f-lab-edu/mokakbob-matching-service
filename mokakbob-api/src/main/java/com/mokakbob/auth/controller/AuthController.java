package com.mokakbob.auth.controller;

import com.mokakbob.auth.controller.request.LoginRequest;
import com.mokakbob.auth.controller.request.SignUpRequest;
import com.mokakbob.auth.controller.response.LoginResponse;
import com.mokakbob.auth.controller.response.SignUpResponse;
import com.mokakbob.auth.service.AuthService;
import com.mokakbob.auth.service.TokenService;
import com.mokakbob.common.path.auth.AuthApiPath;
import com.mokakbob.domain.member.domain.Member;
import jakarta.servlet.http.HttpServletResponse;
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
    private final TokenService tokenService;

    @PostMapping(AuthApiPath.LOGIN)
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        Member member = authService.login(request.email(), request.password());
        String accessToken = tokenService.createAccessToken(member.getId());
        String refreshToken = tokenService.createRefreshToken(member.getId());
        tokenService.addRefreshTokenToCookie(response, refreshToken);

        return ResponseEntity.ok(new LoginResponse(
                accessToken,
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
