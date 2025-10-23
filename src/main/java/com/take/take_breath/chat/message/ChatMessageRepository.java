package com.take.take_breath.chat.message;

import com.take.take_breath.chat.room.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 채팅방의 메세지 목록 조회
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    // 특정 방의 메세지 개수
    int countByChatRoom(ChatRoom chatRoom);

    // 특정 ID보다 큰 메세지 갯수(안읽은 메세지 계산용)
    int countByChatRoomAndIdGreaterThan(ChatRoom chatRoom, Long id);

    // 가장 최신 메세지 조회
    Optional<ChatMessage> findTopByChatRoomOrderByIdDesc(ChatRoom chatRoom);
}
