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
    private String messageType;         // TEXT, IMAGE, FILE
    private String createdAt;
    private boolean isRead;             // 읽음 여부 (UI 표시용)
    private String imageUrl;  // 프로젝트 내부 url을 전달
    private String currentPoint;    // 현재 사용자 남은 포인트에 대해서
}