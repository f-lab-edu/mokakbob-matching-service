package com.mokakbob.chat.controller;

import com.mokakbob.chat.controller.request.ChatRoomEnterRequest;
import com.mokakbob.chat.controller.response.ChatRoomEnterResponse;
import com.mokakbob.common.path.chat.ChatPath;
import com.mokakbob.global.resolver.annotation.MemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    @GetMapping(ChatPath.ENTER)
    public ResponseEntity<ChatRoomEnterResponse> enterRoom(
            @RequestBody ChatRoomEnterRequest request,
            @MemberId Long memberId
    ) {

        return ResponseEntity.ok(new ChatRoomEnterResponse(
                request.roomId(),
                ChatPath.WS,
                ChatPath.PUB,
                ChatPath.SUB
        ));
    }
}
