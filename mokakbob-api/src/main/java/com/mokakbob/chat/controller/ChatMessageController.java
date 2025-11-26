package com.mokakbob.chat.controller;

import com.mokakbob.domain.chat.pubsub.request.ChatMessageRequest;
import com.mokakbob.chat.service.ChatApiService;
import com.mokakbob.metrix.ChatMetrics;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private static final String METRICS_EVENT = "chat_send_message";

    private final ChatApiService chatApiService;
    private final ChatMetrics chatMetrics;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request,
            Principal principal
    ) {
        long sentAt = System.currentTimeMillis();

        try {
            chatMetrics.countRequest(METRICS_EVENT);
            chatApiService.handleMessage(roomId, principal.getName(), request.content(), sentAt);
        } catch (Exception e) {
            chatMetrics.countError(METRICS_EVENT);
            throw e;
        } finally {
            chatMetrics.recordLatency(METRICS_EVENT, System.currentTimeMillis() - sentAt);
        }
    }
}
