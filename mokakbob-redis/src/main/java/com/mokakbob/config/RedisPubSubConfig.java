package com.mokakbob.config;

import com.mokakbob.chat.RedisChatSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisPubSubConfig {

    private static final String CHAT_CHANNEL_PATTERN = "chat.*";

    private final RedisConnectionFactory connectionFactory;
    private final RedisChatSubscriber redisChatSubscriber;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisChatSubscriber, new PatternTopic(CHAT_CHANNEL_PATTERN));
        return container;
    }
}
