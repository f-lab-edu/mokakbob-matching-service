package com.mokakbob.auth.infrastructure.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mokakbob.auth.domain.OauthUser;

public record GItHubUser(
        @JsonProperty("email") String email,
        @JsonProperty("login") String nickName,
        @JsonProperty("avatar_url") String profileImageUrl
) implements OauthUser {

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getNickname() {
        return nickName;
    }

    @Override
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
