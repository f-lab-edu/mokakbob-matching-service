package com.mokakbob.auth.controller;

import com.mokakbob.auth.controller.request.OauthCodeRequest;
import com.mokakbob.auth.controller.response.LoginResponse;
import com.mokakbob.auth.controller.response.SignUpRequireResponse;
import com.mokakbob.auth.controller.response.UrlResponse;
import com.mokakbob.auth.service.GitHubAuthService;
import com.mokakbob.auth.service.TokenService;
import com.mokakbob.auth.service.response.MemberExistResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GitHubOauthController {

    private final GitHubAuthService gitHubAuthService;
    private final TokenService tokenService;

    @GetMapping("/oauth/github/url")
    public ResponseEntity<UrlResponse> getGithubLoginUrl() {
        String url = gitHubAuthService.getLoginUrl();

        return ResponseEntity.ok(new UrlResponse(url));
    }

    @PostMapping("/oauth/github/callback")
    public ResponseEntity<?> callback(
            @RequestBody OauthCodeRequest request,
            HttpServletResponse response
    ) {
        MemberExistResponse MemberExistResponse = gitHubAuthService.loginOrSignUp(request.code());

        if (MemberExistResponse.isMember()) {
            Long memberId = MemberExistResponse.memberId();
            String accessToken = tokenService.createAccessToken(memberId);
            tokenService.createRefreshToken(memberId, response);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + accessToken);

            return ResponseEntity.status(HttpStatus.OK)
                    .headers(httpHeaders)
                    .body(new LoginResponse(
                            memberId,
                            MemberExistResponse.email(),
                            MemberExistResponse.nickName(),
                            MemberExistResponse.profileImage()
                    ));
        } else {
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .body(new SignUpRequireResponse(
                            MemberExistResponse.email(),
                            MemberExistResponse.nickName(),
                            MemberExistResponse.profileImage()
                    ));
        }
    }
}
