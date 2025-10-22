package com.take.take_breath.chat.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class ChatMessageDto {

    @Getter @Setter
    @AllArgsConstructor
    public static class ChatMessageRequest {
        private Long senderId;   // Member id
        private String content;  // 메시지 내용
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class ChatMessageResponse {
        private Long messageId;
        private String senderName;
        private String content;
        private LocalDateTime createdAt;

        // 엔티티 -> DTO 변환
        public static ChatMessageResponse fromEntity(ChatMessage message) {
            return new ChatMessageResponse(
                    message.getId(),
                    message.getSender().getName(),
                    message.getContent(),
                    message.getCreatedAt()
            );
        }
    }
}
