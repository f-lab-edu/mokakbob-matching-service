package com.mokakbob.chat.service;

import com.mokakbob.domain.chat.domain.ChatRoom;
import com.mokakbob.domain.chat.pubsub.ChatPublisher;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import com.mokakbob.domain.chat.service.ChatMessageService;
import com.mokakbob.domain.chat.service.ChatService;
import com.mokakbob.domain.matching.domain.Matching;
import com.mokakbob.domain.matching.service.MatchingParticipateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatApiService {

    private final ChatPublisher chatPublisher;
    private final ChatMessageService messageService;
    private final ChatService chatService;
    private final MatchingParticipateService participateService;

    @Transactional
    public void handleMessage(Long roomId, String memberId, String content, long sendAt) {
        messageService.saveChatMessage(roomId, Long.valueOf(memberId), content);
        chatPublisher.publish(roomId, new ChatMessageResponse(roomId, memberId, content, sendAt));
    }

    @Transactional(readOnly = true)
    public ChatRoom findChatRoom(Long roomId, Long memberId) {
        ChatRoom room = chatService.findChatRoom(roomId);
        Matching matching = room.getMatching();
        participateService.validateMatchingParticipant(matching.getId(), memberId);

        return room;
    }
}
