package com.mokakbob.auth.service;

import com.mokakbob.auth.domain.OauthUser;
import com.mokakbob.auth.infrastructure.GitHubAuthClient;
import com.mokakbob.auth.service.response.MemberExistResponse;
import com.mokakbob.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubAuthService {

    private final GitHubAuthClient client;
    private final MemberService memberService;

    public String getLoginUrl() {
        return client.getLoginUrl();
    }

    public MemberExistResponse loginOrSignUp(String code) {
        String accessToken = client.requestAccessToken(code);
        OauthUser user = client.requestUserInfo(accessToken);

        return memberService.findByNickName(user.getNickname())
                .map(MemberExistResponse::fromMember)
                .orElseGet(() -> MemberExistResponse.fromOauthUser(user));
    }
}
