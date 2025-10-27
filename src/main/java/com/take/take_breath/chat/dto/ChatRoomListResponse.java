package com.take.take_breath.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListResponse {
    private Long roomId;
    private String roomName;
    private Long unreadCount;           // 읽지 않은 메시지 개수
    private String lastMessage;         // 마지막 메시지 내용
    private String lastMessageTime;  // 마지막 메시지 시간

    // 1:1 채팅이므로 상대방 정보도 추가하면 좋음
    private Long otherMemberId;         // 상대방 ID
    private String otherMemberName;     // 상대방 이름
}