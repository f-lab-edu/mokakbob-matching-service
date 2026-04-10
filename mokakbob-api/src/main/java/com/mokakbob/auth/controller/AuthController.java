package com.mokakbob.auth.controller;

import com.mokakbob.auth.controller.request.LoginRequest;
import com.mokakbob.auth.controller.request.SignUpRequest;
import com.mokakbob.auth.controller.response.LoginResponse;
import com.mokakbob.auth.controller.response.SignUpResponse;
import com.mokakbob.auth.controller.response.TokenReissueResponse;
import com.mokakbob.auth.service.AuthService;
import com.mokakbob.auth.service.TokenService;
import com.mokakbob.common.path.auth.AuthPath;
import com.mokakbob.domain.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping(AuthPath.LOGIN)
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        Member member = authService.login(request.email(), request.password());
        Long memberId = member.getId();

        String accessToken = tokenService.createAccessToken(member.getId());
        tokenService.createRefreshToken(memberId, response);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(new LoginResponse(
                        memberId,
                        member.getEmail(),
                        member.getNickname(),
                        member.getProfileImage()
                ));
    }

    @PostMapping(AuthPath.SIGN_UP)
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        Member member = authService.signUp(
                request.email(),
                request.password(),
                request.nickName(),
                request.preference()
        );

        return ResponseEntity.ok(new SignUpResponse(member.getEmail(), member.getNickname()));
    }

    @PostMapping(AuthPath.LOGOUT)
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        tokenService.logout(request);

        return ResponseEntity.ok()
                .build();
    }

    @PostMapping(AuthPath.REISSUE)
    public ResponseEntity<TokenReissueResponse> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String newToken = tokenService.reissue(response, request);

        return ResponseEntity.ok(new TokenReissueResponse(newToken));
    }
}
