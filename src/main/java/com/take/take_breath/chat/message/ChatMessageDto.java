package com.take.take_breath.chat.message;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Base64;

public class ChatMessageDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ChatMessageRequest {
        private Long senderId;
        private String content;  // 메시지 내용
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ChatMessageFileRequest {
        private Long senderId;
        private MultipartFile file; // 이미지 or 일반 파일
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
