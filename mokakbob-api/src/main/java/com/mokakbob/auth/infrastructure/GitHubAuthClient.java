package com.mokakbob.auth.infrastructure;

import com.mokakbob.auth.domain.AuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GitHubAuthClient implements AuthClient {

    private static final String GITHUB_LOGIN_URL = "https://github.com/login/oauth/authorize";

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
                .queryParam("redirect_uri", clientSecret)
                .queryParam("scope", scope)
                .build()
                .toUriString();
    }
}
