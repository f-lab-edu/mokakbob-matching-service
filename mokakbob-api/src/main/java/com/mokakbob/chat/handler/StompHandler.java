package com.mokakbob.chat.handler;

import com.mokakbob.auth.infrastructure.JwtTokenProvider;
import com.mokakbob.chat.exception.ChatErrorCode;
import com.mokakbob.common.exception.ApiException;
import com.mokakbob.metrix.ChatMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.mokakbob.common.constant.AuthConstants;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {


    private final JwtTokenProvider tokenProvider;
    private final ChatMetrics chatMetrics;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        // connect 프레임일 경우, 인증 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader(AuthConstants.AUTH_HEADER);

            Authentication auth = validateToken(token);
            accessor.setUser(auth);
            chatMetrics.incrementSession();
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            chatMetrics.decrementSession();
        }

        return message;
    }

    private Authentication validateToken(String token) {
        if (token == null || !token.startsWith(AuthConstants.BEARER_PREFIX)) {
            throw new ApiException(ChatErrorCode.STOMP_JWT_MISSING);
        }

        token = token.substring(AuthConstants.BEARER_PREFIX.length()); // 접두사 제거

        if (tokenProvider.isAccessTokenExpired(token)) {
            throw new ApiException(ChatErrorCode.STOMP_JWT_EXPIRED);
        }

        try {
            Long memberId = tokenProvider.extractMemberId(token);
            return tokenProvider.getAuthentication(memberId);
        } catch (Exception e) {
            throw new ApiException(ChatErrorCode.STOMP_JWT_INVALID, e);
        }
    }
}
