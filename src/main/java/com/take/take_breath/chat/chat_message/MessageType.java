package com.take.take_breath.chat.chat_message;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MessageType {
    TEXT, IMAGE, FILE, SYSTEM;

    @JsonCreator
    public static MessageType from(String value) {
        return value == null ? TEXT : MessageType.valueOf(value.toUpperCase());
    }
}
