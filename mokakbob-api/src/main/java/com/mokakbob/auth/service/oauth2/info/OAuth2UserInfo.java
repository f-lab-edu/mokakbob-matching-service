package com.mokakbob.auth.service.oauth2.info;

import java.util.Map;

/**
 * 소셜 로그인 제공자로부터 받은 유저 정보를 추상화한 인터페이스입니다.
 */
public interface OAuth2UserInfo {
    
    Map<String, Object> getAttributes();
    
    String getProviderId();
    
    String getProvider();
    
    String getEmail();
    
    String getName();
    
    String getImageUrl();
}
