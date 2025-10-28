package com.mokakbob.chat.handler;

import com.mokakbob.auth.infrastructure.JwtTokenProvider;
import com.mokakbob.chat.exception.ChatErrorCode;
import com.mokakbob.common.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private static final String TOKEN_HEADER_NAME = "Authorization";
    private static final String TOKEN_START_NAME = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = "Bearer ".length();

    private final JwtTokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // connect 프레임일 경우, 인증 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader(TOKEN_HEADER_NAME);

            Authentication auth = validateToken(token);
            accessor.setUser(auth);
        }

        return message;
    }

    private Authentication validateToken(String token) {
        if (token == null || !token.startsWith(TOKEN_START_NAME)) {
            throw new ApiException(ChatErrorCode.STOMP_JWT_MISSING);
        }

        token = token.substring(BEARER_PREFIX_LENGTH); // "Bearer " 제거

        if (tokenProvider.isAccessTokenExpired(token)) {
            throw new ApiException(ChatErrorCode.STOMP_JWT_EXPIRED);
        }

        try {
            Long memberId = tokenProvider.extractMemberId(token);
            return tokenProvider.getAuthentication(memberId);
        } catch (Exception e) {
            throw new ApiException(ChatErrorCode.STOMP_JWT_INVALID);
        }
    }
}
