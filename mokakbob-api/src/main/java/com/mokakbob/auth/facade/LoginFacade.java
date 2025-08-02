package com.mokakbob.auth.facade;

import com.mokakbob.auth.controller.response.LoginResponse;
import com.mokakbob.auth.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginFacade {

    private final TokenService tokenService;

    public ResponseEntity<LoginResponse> successLogin(
            Long memberId,
            HttpServletResponse response,
            String email,
            String nickName,
            String profileImage
    ) {
        String accessToken = tokenService.createAccessToken(memberId);
        tokenService.createRefreshToken(memberId, response);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(new LoginResponse(
                        memberId,
                        email,
                        nickName,
                        profileImage
                ));
    }
}
