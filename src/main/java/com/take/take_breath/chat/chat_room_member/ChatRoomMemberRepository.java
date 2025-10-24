package com.take.take_breath.chat.chat_room_member;

import com.take.take_breath.chat.member.Member;
import com.take.take_breath.chat.room.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    /**
     * 특정 회원이 속한 모든 채팅방 조회
     * - 채팅방 목록을 보여줄 때 사용
     */
    List<ChatRoomMember> findByMemberId(Long memberId);

    /**
     * 특정 채팅방의 특정 회원 정보 조회
     */
    Optional<ChatRoomMember> findByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);

    /**
     * 1:1 채팅방에서 상대방 정보 조회
     * - chatRoomId: 채팅방 ID
     * - memberId: 내 ID
     * - 결과: 나를 제외한 상대방의 ChatRoomMember 정보
     */
    @Query("SELECT crm FROM ChatRoomMember crm " +
            "WHERE crm.chatRoom.id = :chatRoomId " +
            "AND crm.member.id != :memberId")
    ChatRoomMember findOtherMemberInRoom(
            @Param("chatRoomId") Long chatRoomId,
            @Param("memberId") Long memberId
    );

    /**
     * 두 회원 간의 기존 1:1 채팅방 조회 (중복 방지)
     */
    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE cr.roomType = 'PRIVATE' " +
            "AND EXISTS (" +
            "    SELECT 1 FROM ChatRoomMember crm1 " +
            "    WHERE crm1.chatRoom = cr AND crm1.member.id = :memberId1" +
            ") " +
            "AND EXISTS (" +
            "    SELECT 1 FROM ChatRoomMember crm2 " +
            "    WHERE crm2.chatRoom = cr AND crm2.member.id = :memberId2" +
            ") " +
            "AND (SELECT COUNT(crm) FROM ChatRoomMember crm WHERE crm.chatRoom = cr) = 2")
    ChatRoom findExistingPrivateRoom(
            @Param("memberId1") Long memberId1,
            @Param("memberId2") Long memberId2
    );
}
