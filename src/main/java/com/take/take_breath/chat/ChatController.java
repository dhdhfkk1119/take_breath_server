package com.take.take_breath.chat;

import com.take.take_breath.chat.message.ChatMessage;
import com.take.take_breath.chat.room.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class ChatController {
    // 클라 -> 서버 : /app/chat.sendMessage
    // 서버 -> 클라 : /topic/public
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage message) {
        System.out.println("받은 메세지: " + message.getContent());

        // 임시 ChatRoom 객체 - DB 저장 X
        ChatRoom room = new ChatRoom("room1");

        // 받은 메세지를 ChatMessage 엔티티 형태로 반환
        ChatMessage entityMessage = new ChatMessage(
                message.getSender(),
                message.getContent(),
                room
        );

        return message;
    }
}


/*
레거시 - mk1


@Controller
public class ChatController {
    // 클라 -> 서버 : /app/chat.sendMessage
    // 서버 -> 클라 : /topic/public
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage message) {
        System.out.println("받은 메세지: " + message.getContent());

        // 임시 ChatRoom 객체 - DB 저장 X
        ChatRoom room = new ChatRoom("room1");

        // 받은 메세지를 ChatMessage 엔티티 형태로 반환
        ChatMessage entityMessage = new ChatMessage(
                message.getSender(),
                message.getContent(),
                room
        );

        return message;
    }
}

 */