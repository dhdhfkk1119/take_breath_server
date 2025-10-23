package com.take.take_breath.chat.room;

import com.take.take_breath.chat.message.ChatMessage;
import com.take.take_breath.chat.message.ChatMessageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class ChatRoomDto {

    @Getter @Setter
    @AllArgsConstructor
    public static class ChatRoomRequest {
        private String name;
        // private String roomType; // 현재 1:1만 제공
    }

    @Getter @Setter
    @AllArgsConstructor @Builder
    public static class ChatRoomResponse {
        private Long id;
        private String name;
        private String roomType;
        private LocalDateTime createdAt;

        public static ChatRoomResponse fromEntity(ChatRoom room) {
            return ChatRoomResponse.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .roomType(room.getRoomType().name())
                    .createdAt(room.getCreatedAt())
                    .build();
        }
    }
}
