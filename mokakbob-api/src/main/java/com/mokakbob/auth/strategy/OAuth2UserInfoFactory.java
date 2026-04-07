package com.mokakbob.auth.strategy;

import com.mokakbob.auth.exception.AuthApiErrorCode;
import com.mokakbob.auth.strategy.impl.GithubUserInfo;
import com.mokakbob.common.exception.exceptions.ApiException;
import java.util.Map;

/**
 * 소셜 로그인 제공자(RegistrationId)에 따라 적절한UserInfo 구현체를 생성하는 팩토리 클래스입니다.
 */
public class OAuth2UserInfoFactory {

    private static final String GITHUB = "github";

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (GITHUB.equalsIgnoreCase(registrationId)) {
            return new GithubUserInfo(attributes);
        }

        // 지원하지 않는 제공자일 경우 에러 발생
        throw new ApiException(AuthApiErrorCode.FORBIDDEN);
    }
}
