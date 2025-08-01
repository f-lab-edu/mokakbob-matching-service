package com.mokakbob.auth.domain;

public interface AuthClient {

    String getLoginUrl();
    String requestAccessToken(String code);
    OauthUser requestUserInfo(String accessToken);
}
