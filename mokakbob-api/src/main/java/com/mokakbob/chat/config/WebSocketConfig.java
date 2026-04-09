package com.mokakbob.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.chat.RedisChatSubscriber;
import com.mokakbob.chat.handler.StompHandler;
import com.mokakbob.domain.chat.pubsub.ChatSubscriber;
import com.mokakbob.metrix.ChatMetrics;
import io.lettuce.core.cluster.RedisClusterClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        // 구독
        registry.enableSimpleBroker("/sub");
        // 발행
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connect")
                .setAllowedOriginPatterns("*") // CORS 허용
                .withSockJS(); // SockJS fallback 지원

        // test
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        // ChannelInterceptor로 StompHandler 등록
        registration.interceptors(stompHandler);
    }

    @Bean
    public RedisChatSubscriber redisChatSubscriber(
            RedisClusterClient redisClusterClient,
            ObjectMapper objectMapper,
            ChatSubscriber chatSubscriber,
            ChatMetrics chatMetrics
    ) {
        return new RedisChatSubscriber(
                redisClusterClient,
                objectMapper,
                chatSubscriber,
                chatMetrics
        );
    }
}
