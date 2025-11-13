package com.take.take_breath.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ChatResponse {

    @Getter
    @AllArgsConstructor
    public static class CreateChatRoomResponse {
        private Long roomId;
        private String roomName;
    }
}
