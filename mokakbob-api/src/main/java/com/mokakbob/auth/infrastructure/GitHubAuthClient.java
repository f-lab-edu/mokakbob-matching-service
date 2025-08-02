package com.mokakbob.auth.infrastructure;

import com.mokakbob.auth.domain.AuthClient;
import com.mokakbob.auth.domain.OauthUser;
import com.mokakbob.auth.infrastructure.response.GItHubUser;
import com.mokakbob.auth.infrastructure.response.GithubAccessTokenResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class GitHubAuthClient implements AuthClient {

    private static final String GITHUB_LOGIN_URL = "https://github.com/login/oauth/authorize";
    private static final String GITHUB_TOKEN_URL = "/login/oauth/access_token";
    private static final String REQUEST_USER_URI = "/user";

    private final WebClient githubTokenWebClient;
    private final WebClient githubApiWebClient;

    @Value("${oauth.github.client.id}")
    private String clientId;

    @Value("${oauth.github.client.secret}")
    private String clientSecret;

    @Value("${oauth.github.redirect.url}")
    private String redirectUrl;

    @Value("${oauth.github.scope}")
    private String scope;

    @Override
    public String getLoginUrl() {
        return UriComponentsBuilder.fromUriString(GITHUB_LOGIN_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUrl)
                .queryParam("scope", scope)
                .build()
                .toUriString();
    }

    @Override
    public String requestAccessToken(String code) {
        return githubTokenWebClient
                .post()
                .uri(GITHUB_TOKEN_URL)
                .bodyValue(Map.of(
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "code", code,
                        "redirect_uri", redirectUrl
                ))
                .retrieve()
                .bodyToMono(GithubAccessTokenResponse.class)
                .map(GithubAccessTokenResponse::accessToken)
                .block();
    }

    @Override
    public OauthUser requestUserInfo(String accessToken) {
        return githubApiWebClient
                .get()
                .uri(REQUEST_USER_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GItHubUser.class)
                .block();
    }
}
