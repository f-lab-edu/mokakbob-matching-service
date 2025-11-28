package com.mokakbob.domain.chat.repository;

import com.mokakbob.domain.chat.domain.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
                select cr
                from ChatRoom cr
                join fetch cr.matching m
                where m.id in :matchingIds
            """)
    List<ChatRoom> findAllWithMatchingByMatchingIdIn(@Param("matchingIds") List<Long> matchingIds);
}
