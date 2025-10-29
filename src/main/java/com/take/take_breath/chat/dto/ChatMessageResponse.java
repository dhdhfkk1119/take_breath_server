package com.take.take_breath.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageResponse {
    private Long messageId;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType;
    private String createdAt;
    private boolean isRead;
    private String imageUrl;
    private String currentPoint;
}