package com.take.take_breath.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDto {
    private Long senderId;   // Member id
    private String content;  // 메시지 내용
}
