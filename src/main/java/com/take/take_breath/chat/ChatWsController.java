package com.take.take_breath.chat;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.ApiUtil.ApiResult;
import com.take.take_breath.chat.message.ChatMessage;
import com.take.take_breath.chat.message.ChatMessageDto.ChatMessageRequest;
import com.take.take_breath.chat.message.ChatMessageDto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWsController {
    private final ChatService chatService;

    // 메세지 전송
    @MessageMapping("/chat.sendMessage.{roomId}")
    @SendTo("/topic/room.{roomId}")
    public ApiResult<ChatMessageResponse> sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request
    ) {
        System.out.println("[방ID : " + roomId + ", 이름 : " + request.getSenderId() + "] 메세지 : " + request.getContent());
        ChatMessage chat = chatService.saveMessage(request.getSenderId(), request.getContent(), roomId);
        ChatMessageResponse response = ChatMessageResponse.fromEntity(chat);
        return ApiUtil.success(response);
    }
}

/*

서버 전달 메세지
    {
      "id": 10,
      "content": "안녕하세요!",
      "chatRoom": {
        "id": 1,
        "name": "room1"
      },
      "sender": {
        "id": 5,
        "email": "user1@test.com",
        "name": "홍길동",
        "role": "USER",
        "status": "ACTIVE"
      }
    }

*/