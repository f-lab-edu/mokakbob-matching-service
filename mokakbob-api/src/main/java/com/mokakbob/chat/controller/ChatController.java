package com.mokakbob.chat.controller;

import com.mokakbob.domain.chat.pubsub.request.ChatMessageRequest;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import com.mokakbob.chat.service.ChatApiService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatApiService chatApiService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request,
            Principal principal
    ) {
        chatApiService.handleMessage(roomId, principal.getName(), request.content());
        chatApiService.broadCastMessage(roomId, new ChatMessageResponse(roomId, principal.getName(), request.content()));
    }
}
