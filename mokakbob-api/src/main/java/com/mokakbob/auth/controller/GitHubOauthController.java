package com.mokakbob.auth.controller;

import com.mokakbob.auth.controller.request.OauthCodeRequest;
import com.mokakbob.auth.controller.response.SignUpRequireResponse;
import com.mokakbob.auth.controller.response.UrlResponse;
import com.mokakbob.auth.facade.LoginFacade;
import com.mokakbob.auth.service.GitHubAuthService;
import com.mokakbob.auth.service.response.MemberExistResponse;
import com.mokakbob.common.path.auth.OauthPath;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GitHubOauthController {

    private static final String SIGN_UP_REQUIRED = "SIGN_UP_REQUIRED";

    private final GitHubAuthService gitHubAuthService;
    private final LoginFacade loginFacade;

    @GetMapping(OauthPath.GITHUB_URL)
    public ResponseEntity<UrlResponse> getGithubLoginUrl() {
        String url = gitHubAuthService.getLoginUrl();

        return ResponseEntity.ok(new UrlResponse(url));
    }

    @PostMapping(OauthPath.GITHUB_CALL_BACK)
    public ResponseEntity<?> callback(
            @RequestBody OauthCodeRequest request,
            HttpServletResponse response
    ) {
        MemberExistResponse MemberExistResponse = gitHubAuthService.loginOrSignUp(request.code());

        if (MemberExistResponse.isMember()) {
            return loginFacade.successLogin(
                    MemberExistResponse.memberId(),
                    response,
                    MemberExistResponse.email(),
                    MemberExistResponse.nickName(),
                    MemberExistResponse.profileImage()
            );
        } else {
            return ResponseEntity.ok(new SignUpRequireResponse(
                    SIGN_UP_REQUIRED,
                    MemberExistResponse.email(),
                    MemberExistResponse.nickName(),
                    MemberExistResponse.profileImage()
            ));
        }
    }
}
