package com.mokakbob.domain.chat.repository;

import com.mokakbob.domain.chat.domain.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByMatchingIdIn(List<Long> matchingIds);
}
