package com.mokakbob.chat.controller;

import com.mokakbob.domain.chat.pubsub.request.ChatMessageRequest;
import com.mokakbob.chat.service.ChatApiService;
import com.mokakbob.metrix.ChatMetrics;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private static final String METRICS_EVENT = "chat_send";

    private final ChatApiService chatApiService;
    private final ChatMetrics chatMetrics;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request,
            Principal principal
    ) {
        long start = System.currentTimeMillis();

        try {
            chatMetrics.countRequest(METRICS_EVENT);
            chatApiService.handleMessage(roomId, principal.getName(), request.content());
        } catch (Exception e) {
            chatMetrics.countError(METRICS_EVENT);
            throw e;
        } finally {
            chatMetrics.recordLatency(METRICS_EVENT, System.currentTimeMillis() - start);
        }
    }
}
