package com.mokakbob.domain.chat.pubsub;

import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;

public interface ChatSubscriber {
    void handleMessage(String topic, ChatMessageResponse response);
}
