package com.take.take_breath.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageMessageResponse {
    private Long messageId;
    private Long senderId;
    private String senderName;
    private String messageType;
    private String createdAt;
    private boolean isRead;
    private String imageUrl;
    private String currentPoint;
}