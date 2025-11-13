package com.take.take_breath.chat.chat_room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
            SELECT cr FROM ChatRoom cr
            WHERE cr.roomType = 'COUNSELING'
            AND EXISTS (
                SELECT 1 FROM ChatRoomMember crm1
                WHERE crm1.chatRoom = cr AND crm1.member.id = :memberId
            )
            AND EXISTS (
                SELECT 1 FROM ChatRoomMember crm2
                WHERE crm2.chatRoom = cr AND crm2.member.id = :consultantId
            )
            AND (
                SELECT COUNT(crm3) FROM ChatRoomMember crm3
                WHERE crm3.chatRoom = cr
            ) = 2
            """)
    Optional<ChatRoom> findDirectRoomBetweenMembers(
            @Param("memberId") Long memberId,
            @Param("consultantId") Long consultantId
    );
}
