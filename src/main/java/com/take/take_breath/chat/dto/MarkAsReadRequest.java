package com.take.take_breath.chat.dto;


import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class MarkAsReadRequest {
    private Long chatRoomId;
    private Long memberId;
    private Long lastMessageId;     // 마지막으로 읽은 메시지 ID
}