package com.mokakbob.auth.service.response;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public record MemberExistResponse(
        boolean isMember,
        Long memberId,
        String email,
        String nickName,
        String profileImage
) implements OAuth2User {

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(
                "email", email,
                "name", nickName,
                "picture", profileImage
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return nickName;
    }

    public static MemberExistResponse fromMember(Long memberId, String email, String nickName, String profileImage) {
        return new MemberExistResponse(
                true,
                memberId,
                email,
                nickName,
                profileImage
        );
    }

    public static MemberExistResponse fromOauthUser(String email, String nickName, String profileImage) {
        return new MemberExistResponse(
                false,
                null,
                email,
                nickName,
                profileImage
        );
    }
}
