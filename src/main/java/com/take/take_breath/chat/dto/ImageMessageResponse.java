package com.take.take_breath.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ImageMessageResponse {
    private Long messageId;
    private Long senderId;
    private String senderName;
    private String messageType;         // IMAGE
    private String imageUrl;            // 이미지 다운로드 URL
    private String createdAt;
    private boolean isRead;
}