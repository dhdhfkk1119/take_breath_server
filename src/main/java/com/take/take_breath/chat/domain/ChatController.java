package com.take.take_breath.chat.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    // 클라이언트 -> 서버로 보낼 때 : /app/chat.send
    // 서버 -> 클라이언트로 보낼 떄

    @MessageMapping("/chat.send")   // 클라가 전달한 메세지를 받아서
    @SendTo("topic/public")         // 해당 채널로 보낸다
    public String sendMessage(String message) {
        return message;
    }

}
