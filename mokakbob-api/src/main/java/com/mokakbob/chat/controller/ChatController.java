package com.mokakbob.chat.controller;

import com.mokakbob.chat.controller.request.ChatMessageRequest;
import com.mokakbob.chat.controller.response.ChatMessageResponse;
import com.mokakbob.chat.service.ChatApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatApiService chatApiService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageRequest request) {
        chatApiService.handleMessage(roomId, request.nickName(), request.content());
        chatApiService.broadCastMessage(roomId, new ChatMessageResponse(roomId, request.nickName(), request.content()));
    }
}
