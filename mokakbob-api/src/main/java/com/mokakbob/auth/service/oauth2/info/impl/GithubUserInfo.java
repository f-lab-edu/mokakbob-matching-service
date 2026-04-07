package com.mokakbob.auth.service.oauth2.info.impl;

import com.mokakbob.auth.service.oauth2.info.OAuth2UserInfo;
import java.util.Map;

/**
 * GitHub 소셜 로그인 유저 정보를 처리하는 구현체입니다.
 */
public class GithubUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GithubUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("login");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
