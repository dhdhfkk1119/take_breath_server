package com.take.take_breath.chat;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.ApiUtil.ApiResult;
import com.take.take_breath.chat.dto.ChatMessageRequest;
import com.take.take_breath.chat.dto.ChatMessageResponse;
import com.take.take_breath.chat.dto.MarkAsReadRequest;
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
        ChatMessageResponse response = chatService.sendMessage(request);
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