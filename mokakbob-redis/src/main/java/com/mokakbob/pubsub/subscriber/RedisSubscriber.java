package com.mokakbob.pubsub.subscriber;

import com.mokakbob.pubsub.RedisChannelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String body = new String(message.getBody());

        String memberId = channel.substring("channel:notification:user:".length());
        messagingTemplate.convertAndSend(
                RedisChannelType.NOTIFICATION_USER.formatDestination(memberId),
                body
        );
    }
}
