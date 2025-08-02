package com.mokakbob.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final String GITHUB_TOKEN_BASE_URL = "https://github.com";
    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    @Bean
    public WebClient githubTokenWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(GITHUB_TOKEN_BASE_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient githubApiWebClient(WebClient.Builder builder){
        return builder
                .baseUrl(GITHUB_API_BASE_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
