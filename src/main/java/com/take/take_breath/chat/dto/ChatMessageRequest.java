package com.take.take_breath.chat.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageRequest {
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private String messageType;     // TEXT, IMAGE, FILE (기본값: TEXT)
}