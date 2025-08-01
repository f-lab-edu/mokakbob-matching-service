package com.mokakbob.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";

    @Bean
    public WebClient githubWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(GITHUB_TOKEN_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
