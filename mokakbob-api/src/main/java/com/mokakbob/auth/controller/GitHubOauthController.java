package com.mokakbob.auth.controller;

import com.mokakbob.auth.controller.response.UrlResponse;
import com.mokakbob.auth.service.GitHubAuthService;
import com.mokakbob.common.path.auth.GitHubOauthApiPath;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GitHubOauthController {

    private final GitHubAuthService gitHubAuthService;

    @GetMapping(GitHubOauthApiPath.URL)
    public ResponseEntity<UrlResponse> getGithubLoginUrl() {
        String url = gitHubAuthService.getLoginUrl();

        return ResponseEntity.ok(new UrlResponse(url));
    }
}
