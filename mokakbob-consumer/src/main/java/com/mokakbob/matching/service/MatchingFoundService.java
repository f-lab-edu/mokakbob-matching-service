package com.mokakbob.matching.service;


import com.mokakbob.domain.chat.domain.ChatRoom;
import com.mokakbob.domain.chat.service.ChatService;
import com.mokakbob.domain.matching.domain.Matching;
import com.mokakbob.domain.matching.event.MatchingFoundEvent;
import com.mokakbob.domain.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingFoundService {

    private final NotificationCreateService notificationCreateService;
    private final MatchingService matchingService;
    private final ChatService chatService;

    @Transactional
    public void handleMatchingFound(MatchingFoundEvent event) {
        Matching matching = matchingService.saveMatching(event.category());
        matchingService.saveMatchingParticipants(matching, event.matched());

        ChatRoom chatRoom = chatService.saveChatRoom(matching);

        notificationCreateService.createNotification(event);

        log.info("매칭 성사됨: matchingId={}, chatRoomId={}, notifiedMembers={}",
                matching.getId(), chatRoom.getId(), event.matched());
    }
}
