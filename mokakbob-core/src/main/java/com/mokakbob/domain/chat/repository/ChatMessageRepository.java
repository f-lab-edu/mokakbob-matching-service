package com.mokakbob.domain.chat.repository;

import com.mokakbob.domain.chat.domain.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // cursor가 없는 경우
    @Query("""
        SELECT m
        FROM ChatMessage m
        WHERE m.chatRoomId = :roomId
        ORDER BY m.createdAt DESC, m.id DESC
    """)
    List<ChatMessage> findLatestMessages(
            @Param("roomId") Long roomId,
            Pageable pageable
    );

    // cursor가 있는 경우
    @Query("""
        SELECT m
        FROM ChatMessage m
        WHERE m.chatRoomId = :roomId
          AND (
                m.createdAt < :cursorCreatedAt
                OR (m.createdAt = :cursorCreatedAt AND m.id < :cursorId)
          )
        ORDER BY m.createdAt DESC, m.id DESC
    """)
    List<ChatMessage> findMessagesByCursor(
            @Param("roomId") Long roomId,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
