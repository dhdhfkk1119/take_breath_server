package com.take.take_breath.chat.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Base64;

public class ChatMessageDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ChatMessageRequest {
        private Long senderId;   // Member id
        private String content;  // 메시지 내용
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ChatMessageResponse {

        private Long id;
        private String content;
        private String senderName;
        private Long roomId;
        private String type;
        private String attachmentBase64;
        private LocalDateTime createdAt;

        // 엔티티 -> DTO 변환
        public static ChatMessageResponse fromEntity(ChatMessage chat) {
            String base64 = null;
            if (chat.getAttachmentData() != null) {
                base64 = Base64.getEncoder().encodeToString(chat.getAttachmentData());
            }

            return ChatMessageResponse.builder()
                    .id(chat.getId())
                    .content(chat.getContent())
                    .senderName(chat.getSender().getName())
                    .roomId(chat.getChatRoom().getId())
                    .type(chat.getType().name())
                    .attachmentBase64(base64)
                    .createdAt(chat.getCreatedAt())
                    .build();
        }
    }
}
