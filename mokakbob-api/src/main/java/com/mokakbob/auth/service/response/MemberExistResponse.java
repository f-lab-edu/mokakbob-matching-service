package com.mokakbob.auth.service.response;

import com.mokakbob.auth.domain.OauthUser;
import com.mokakbob.domain.member.domain.Member;

public record MemberExistResponse(
        boolean isMember,
        Long memberId,
        String email,
        String nickName,
        String profileImage
) {

    public static MemberExistResponse fromMember(Member member) {
        return new MemberExistResponse(
                true,
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImage()
        );
    }

    public static MemberExistResponse fromOauthUser(OauthUser user) {
        return new MemberExistResponse(
                false,
                null,
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl()
        );
    }
}
