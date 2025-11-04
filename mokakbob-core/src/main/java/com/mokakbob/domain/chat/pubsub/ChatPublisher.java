package com.mokakbob.domain.chat.pubsub;

import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;

public interface ChatPublisher {
    void publish(Long roomId, ChatMessageResponse response);
}
