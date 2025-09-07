package com.mokakbob.domain.chat.service;

import com.mokakbob.domain.chat.domain.ChatRoom;
import com.mokakbob.domain.chat.repository.ChatRoomRepository;
import com.mokakbob.domain.matching.domain.Matching;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoom saveChatRoom(Matching matching) {
        ChatRoom chatRoom = ChatRoom.builder()
                .matching(matching)
                .build();

        return chatRoomRepository.save(chatRoom);
    }
}
