package com.take.take_breath.chat;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.ApiUtil.ApiResult;
import com.take.take_breath.chat.dto.ChatMessageRequest;
import com.take.take_breath.chat.dto.ChatMessageResponse;
import com.take.take_breath.chat.dto.MarkAsReadRequest;
import com.take.take_breath.chat.message.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWsController {
    private final ChatService chatService;

    /**
     * 메시지 전송
     * Client -> /app/chat.sendMessage.{roomId}
     * Server -> /topic/room.{roomId} (구독자들에게 브로드캐스트)
     */
    /*
        stompClient.send('/app/chat.sendMessage.1', {}, JSON.stringify({
          chatRoomId: 1,
          senderId: 1,
          content: '안녕하세요',
          messageType: 'TEXT'
        }));
     */
    @MessageMapping("/chat.sendMessage.{roomId}")
    @SendTo("/topic/room.{roomId}")
    public ApiResult<ChatMessageResponse> sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request) {

        System.out.println("[방ID: " + roomId +
                ", 발신자ID: " + request.getSenderId() +
                "] 메시지: " + request.getContent());

        // 1. 메시지 저장 (자동으로 발신자는 읽음 처리됨)
        ChatMessage savedMessage = chatService.sendMessage(request);

        // 2. Entity -> DTO 변환
        ChatMessageResponse response = ChatMessageResponse.builder()
                .messageId(savedMessage.getId())
                .senderId(savedMessage.getSender().getId())
                .senderName(savedMessage.getSender().getName())
                .content(savedMessage.getContent())
                .messageType(savedMessage.getType().name())
                .createdAt(savedMessage.getCreatedAt())
                .isRead(true)  // 발신자는 항상 읽음
                .build();

        // 3. 구독자들에게 브로드캐스트
        return ApiUtil.success(response);
    }

    /**
     * 읽음 처리 알림
     * Client -> /app/chat.markAsRead.{roomId}
     * Server -> /topic/room.{roomId}.read (읽음 상태 알림)
     */
    @MessageMapping("/chat.markAsRead.{roomId}")
    @SendTo("/topic/room.{roomId}.read")
    public ApiResult<String> markAsRead(
            @DestinationVariable Long roomId,
            MarkAsReadRequest request) {

        chatService.markAsRead(
                request.getChatRoomId(),
                request.getMemberId(),
                request.getLastMessageId()
        );

        return ApiUtil.success(
                "Member " + request.getMemberId() +
                        " read until message " + request.getLastMessageId()
        );
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