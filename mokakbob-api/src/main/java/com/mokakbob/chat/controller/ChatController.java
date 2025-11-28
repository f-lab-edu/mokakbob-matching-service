package com.mokakbob.chat.controller;

import com.mokakbob.chat.controller.request.ChatRoomEnterRequest;
import com.mokakbob.chat.controller.response.ChatRoomEnterResponse;
import com.mokakbob.chat.controller.response.ChatRoomResponses;
import com.mokakbob.chat.service.ChatApiService;
import com.mokakbob.common.path.chat.ChatPath;
import com.mokakbob.domain.chat.domain.ChatRoom;
import com.mokakbob.global.resolver.annotation.MemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatApiService chatApiService;

    @GetMapping(ChatPath.ENTER)
    public ResponseEntity<ChatRoomEnterResponse> enterRoom(
            @RequestBody ChatRoomEnterRequest request,
            @MemberId Long memberId
    ) {
        ChatRoom room = chatApiService.findChatRoom(request.roomId(), memberId);

        return ResponseEntity.ok(new ChatRoomEnterResponse(
                room.getId(),
                ChatPath.WS,
                ChatPath.PUB,
                ChatPath.SUB
        ));
    }

    @GetMapping(ChatPath.ROOMS)
    public ResponseEntity<ChatRoomResponses> searchRooms(
            @MemberId Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(chatApiService.findChatRooms(memberId, page, size));
    }
}
