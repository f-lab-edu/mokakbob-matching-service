package com.mokakbob.chat.service;

import com.mokakbob.chat.controller.response.ChatParticipantResponse;
import com.mokakbob.chat.controller.response.ChatRoomResponse;
import com.mokakbob.domain.chat.domain.ChatRoom;
import com.mokakbob.domain.chat.pubsub.ChatPublisher;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import com.mokakbob.domain.chat.service.ChatMessageService;
import com.mokakbob.domain.chat.service.ChatService;
import com.mokakbob.domain.matching.domain.Matching;
import com.mokakbob.domain.matching.domain.MatchingParticipant;
import com.mokakbob.domain.matching.service.MatchingParticipateService;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.service.MemberService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    private final MemberService memberService;

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

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> findChatRooms(Long memberId) {
        List<MatchingParticipant> participants = participateService.findMatchingParticipantsByMemberId(memberId);
        List<Long> matchingIds = participants.stream()
                .map(MatchingParticipant::getMatchingId)
                .toList();

        if (matchingIds.isEmpty()) {
            return List.of();
        }

        List<ChatRoom> rooms = chatService.findMatchingChatRooms(matchingIds);

        List<MatchingParticipant> allParticipants = participateService.findMatchingParticipantByMatchingIds(
                matchingIds);
        Map<Long, List<MatchingParticipant>> groupedParticipants = loadParticipantsGroupedByMatchingId(allParticipants);
        Map<Long, Member> members = loadMembersAsMap(allParticipants);

        return rooms.stream()
                .map(room -> {
                    Long matchingId = room.getMatching().getId();

                    List<MatchingParticipant> matchingParticipants = groupedParticipants.getOrDefault(matchingId,
                            List.of());

                    List<ChatParticipantResponse> participantResponses =
                            matchingParticipants.stream()
                                    .map(p -> ChatParticipantResponse.of(
                                            p,
                                            members.get(p.getMemberId())
                                    ))
                                    .toList();

                    return ChatRoomResponse.of(
                            room,
                            room.getMatching(),
                            participantResponses
                    );
                })
                .toList();
    }

    private Map<Long, List<MatchingParticipant>> loadParticipantsGroupedByMatchingId(
            List<MatchingParticipant> allParticipants) {
        return allParticipants.stream()
                .collect(Collectors.groupingBy(MatchingParticipant::getMatchingId));
    }

    private Map<Long, Member> loadMembersAsMap(List<MatchingParticipant> allParticipants) {
        List<Long> memberIds = allParticipants.stream()
                .map(MatchingParticipant::getMemberId)
                .distinct()
                .toList();

        List<Member> members = memberService.findMembers(memberIds);

        return members.stream()
                .collect(Collectors.toMap(Member::getId, m -> m));
    }
}
