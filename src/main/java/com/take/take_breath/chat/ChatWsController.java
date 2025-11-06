package com.take.take_breath.chat;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.ApiUtil.ApiResult;
import com.take.take_breath.chat.dto.ChatMessageRequest;
import com.take.take_breath.chat.dto.ChatMessageResponse;
import com.take.take_breath.chat.dto.MarkAsReadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatWsController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 전송
     * Client -> /app/chat.sendMessage.{roomId}
     * Server -> /topic/room.{roomId} (구독자들에게 브로드캐스트)
     */
    @MessageMapping("/pub/chat.sendMessage.{roomId}")
    @SendTo("/sub/chat/room.{roomId}")
    public ApiResult<ChatMessageResponse> sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");
        ChatMessageResponse response = chatService.sendMessage(roomId, memberId, request);
        return ApiUtil.success(response);
    }

    /**
     * 읽음 처리 알림
     * Client -> /app/chat.markAsRead.{roomId}
     * Server -> /topic/room.{roomId}.read (읽음 상태 알림)
     */
    /*
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
    */
}