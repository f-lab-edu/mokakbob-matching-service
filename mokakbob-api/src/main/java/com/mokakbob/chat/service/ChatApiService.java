package com.mokakbob.chat.service;

import com.mokakbob.chat.controller.response.ChatRoomResponse;
import com.mokakbob.chat.controller.response.ChatRoomResponses;
import com.mokakbob.chat.service.support.ParticipantContext;
import com.mokakbob.chat.util.ChatRoomMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatApiService {

    private static final String PAGING_SORT_DELIMITER = "id";

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

    /**
     * 사용자가 참여한 채팅방 목록을 Offset 기반으로 페이징하여 조회한다.
     *
     * <p>조회 기준은 {@link MatchingParticipant} 엔티티이며,
     * MatchingParticipant → Matching → ChatRoom 이 1:1 대응되는 구조를 활용한다.</p>
     *
     * <p>페이징된 MatchingParticipant 목록에서 matchingId를 추출하여
     * 해당 매칭의 채팅방(ChatRoom)을 조회하고, 이후 각 매칭에 참여한 사용자 목록과 회원(Member) 정보를 Bulk 조회 후 In-memory 매핑하여 응답을 구성한다.</p>
     *
     * @param memberId 조회할 사용자의 ID
     * @param page     페이지 번호(0-based)
     * @param size     페이지 크기
     * @return 페이징된 채팅방 응답 목록
     */
    @Transactional(readOnly = true)
    public ChatRoomResponses findChatRooms(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(PAGING_SORT_DELIMITER).descending());

        Page<MatchingParticipant> participantPage =
                participateService.findMatchingParticipantsByMemberId(memberId, pageable);

        if (participantPage.isEmpty()) {
            return ChatRoomResponses.empty(memberId, pageable);
        }

        List<Long> matchingIds = extractMatchingIds(participantPage.getContent());
        List<ChatRoom> rooms = chatService.findMatchingChatRooms(matchingIds);
        ParticipantContext context = loadParticipantContext(matchingIds);
        List<ChatRoomResponse> responses = mapToChatRoomResponses(rooms, context);

        return ChatRoomResponses.of(
                memberId,
                responses,
                participantPage.getNumber(),
                participantPage.getSize(),
                participantPage.getTotalElements(),
                participantPage.getTotalPages(),
                participantPage.isLast()
        );
    }

    private List<Long> extractMatchingIds(List<MatchingParticipant> participants) {
        return participants.stream()
                .map(MatchingParticipant::getMatchingId)
                .distinct()
                .toList();
    }

    private ParticipantContext loadParticipantContext(List<Long> matchingIds) {
        List<MatchingParticipant> participants = participateService.findMatchingParticipantByMatchingIds(matchingIds);

        Map<Long, List<MatchingParticipant>> participantsByMatchingId = participants.stream()
                .collect(Collectors.groupingBy(MatchingParticipant::getMatchingId));
        Map<Long, Member> memberMap = loadMembersAsMap(participants);

        return new ParticipantContext(participantsByMatchingId, memberMap);
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

    private List<ChatRoomResponse> mapToChatRoomResponses(List<ChatRoom> rooms, ParticipantContext context) {
        return rooms.stream()
                .map(room -> {
                    Long matchingId = room.getMatching().getId();
                    List<MatchingParticipant> participants = context.participantsByMatchingId()
                            .getOrDefault(matchingId, List.of());

                    return ChatRoomMapper.toChatRoomResponse(
                            room,
                            participants,
                            context.memberMap()
                    );
                })
                .toList();
    }
}
