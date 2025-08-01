package com.mokakbob.auth.service;

import com.mokakbob.auth.infrastructure.GitHubAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubAuthService {

    private final GitHubAuthClient client;

    public String getLoginUrl() {
        return client.getLoginUrl();
    }
}
