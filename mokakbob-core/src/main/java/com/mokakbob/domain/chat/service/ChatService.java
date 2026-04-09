package com.mokakbob.domain.chat.service;

import com.mokakbob.domain.exception.DomainException;
import com.mokakbob.domain.chat.domain.ChatRoom;
import com.mokakbob.domain.chat.exception.ChatErrorCode;
import com.mokakbob.domain.chat.repository.ChatRoomRepository;
import com.mokakbob.domain.matching.domain.Matching;
import java.util.List;
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

    @Transactional(readOnly = true)
    public ChatRoom findChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new DomainException(ChatErrorCode.NOT_FOUND_CHAT_ROOM));
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> findMatchingChatRooms(List<Long> matchingIds) {
        return chatRoomRepository.findAllWithMatchingByMatchingIdIn(matchingIds);
    }
}
