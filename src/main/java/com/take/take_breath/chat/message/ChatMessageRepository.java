package com.take.take_breath.chat.message;

import com.take.take_breath.chat.room.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 방의 메세지 개수
    int countByChatRoom(ChatRoom chatRoom);

    // 특정 ID보다 큰 메세지 갯수(안읽은 메세지 계산용)
    int countByChatRoomAndIdGreaterThan(ChatRoom chatRoom, Long id);

    // 가장 최신 메세지 조회
    Optional<ChatMessage> findTopByChatRoomOrderByIdDesc(ChatRoom chatRoom);

    /**
     * 특정 채팅방의 모든 메시지 조회 (시간순 정렬)
     */
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    /**
     * 읽지 않은 메시지 개수 계산
     * - chatRoomId: 채팅방 ID
     * - lastReadMessageId: 내가 마지막으로 읽은 메시지 ID (null이면 모든 메시지가 안읽음)
     * - memberId: 내 ID (내가 보낸 메시지는 제외)
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m " +
            "WHERE m.chatRoom.id = :chatRoomId " +
            "AND (:lastReadMessageId IS NULL OR m.id > :lastReadMessageId) " +
            "AND m.sender.id != :memberId")
    Long countUnreadMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("lastReadMessageId") Long lastReadMessageId,
            @Param("memberId") Long memberId
    );

    /**
     * 채팅방의 마지막 메시지 조회
     * - 채팅방 목록에서 "마지막 메시지 미리보기"를 보여주기 위함
     */
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE m.chatRoom.id = :chatRoomId " +
            "ORDER BY m.createdAt DESC " +
            "LIMIT 1")
    ChatMessage findLastMessageByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}
