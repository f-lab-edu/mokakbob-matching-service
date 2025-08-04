package com.mokakbob.auth.service;

import com.mokakbob.auth.service.response.MemberExistResponse;
import com.mokakbob.domain.member.service.MemberService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(request);
        Map<String, Object> attr = user.getAttributes();

        String email = (String) attr.get("email");
        String login = (String) attr.get("login");
        String avatar = (String) attr.get("avatar_url");

        return memberService.findByNickName(login)
                .map(m -> MemberExistResponse.fromMember(m.getId(), m.getEmail(), m.getNickname(), m.getProfileImage()))
                .orElseGet(() -> MemberExistResponse.fromOauthUser(email, login, avatar));
    }
}
