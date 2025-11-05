package com.take.take_breath.chat.dto;

import com.take.take_breath.chat.chat_message.MessageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {
    private String content;
    private MessageType messageType;     // TEXT, IMAGE, FILE (기본값: TEXT)
}