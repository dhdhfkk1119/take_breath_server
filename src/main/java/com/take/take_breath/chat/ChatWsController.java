package com.take.take_breath.chat;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.ApiUtil.ApiResult;
import com.take.take_breath.chat.dto.ChatMessageRequest;
import com.take.take_breath.chat.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWsController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 전송
     * Client -> Server = /pub/chat.sendMessage.{roomId}
     * Server -> Channel /sub/chat/room.{roomId} (구독자들에게 브로드캐스트)
     */
    @MessageMapping("/chat.sendMessage.{roomId}")   // 설정에 prefix로 /pub
    @SendTo("/sub/chat/room.{roomId}")
    public ApiResult<ChatMessageResponse> sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");
        log.info("메세지 받음 : {}", request.getMessageType());
        log.info("사용자 : {}", memberId);
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