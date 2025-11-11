package com.take.take_breath.chat.dto;

import com.take.take_breath.chat.chat_message.MessageType;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long messageId;
    private Long senderId;
    private String senderName;
    private String content;
    private MessageType messageType;
    private Timestamp createdAt;
    private Boolean isRead;
    private String attachmentPath;
    private Long currentPoint;
}