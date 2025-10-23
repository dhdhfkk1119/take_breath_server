package com.take.take_breath.chat.chat_room_member;

import com.take.take_breath.chat.member.Member;
import com.take.take_breath.chat.room.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    // 특정 사용자의 특정 방 정보 조회
    Optional<ChatRoomMember> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    // 특정 사용자가 속한 모든 방 조회
    List<ChatRoomMember> findByMember(Member member);

    // 특정 방의 모든 멤버 조회
    List<ChatRoomMember> findByChatRoom(ChatRoom chatRoom);
}
