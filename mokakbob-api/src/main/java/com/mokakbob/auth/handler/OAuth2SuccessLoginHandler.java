package com.mokakbob.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.auth.handler.response.SignUpRequireResponse;
import com.mokakbob.auth.handler.response.SuccessLoginResponse;
import com.mokakbob.auth.service.TokenService;
import com.mokakbob.auth.service.response.MemberExistResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessLoginHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        MemberExistResponse existResponse = (MemberExistResponse) ((OAuth2AuthenticationToken) authentication).getPrincipal();

        if (existResponse.isMember()) {
            Long memberId = existResponse.memberId();
            String accessToken = tokenService.createAccessToken(memberId);
            tokenService.createRefreshToken(memberId, response);

            response.setHeader("Authorization", "Bearer " + accessToken);

            writeResponse(response, new SuccessLoginResponse(
                    true,
                    memberId,
                    existResponse.email(),
                    existResponse.nickName(),
                    existResponse.profileImage()
            ));
        } else {
            writeResponse(response, new SignUpRequireResponse(
                    true,
                    existResponse.email(),
                    existResponse.nickName(),
                    existResponse.profileImage()
            ));
        }
    }

    private void writeResponse(HttpServletResponse response, Object body) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
