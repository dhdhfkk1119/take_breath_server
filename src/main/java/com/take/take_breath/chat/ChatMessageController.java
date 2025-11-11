package com.take.take_breath.chat;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.chat.dto.*;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chat/messages")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatService chatService;

    /**
     * 채팅방의 메시지 목록 조회 (읽음 여부 포함)
     * GET /api/chat/messages/1
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getMessages(@PathVariable(name = "roomId") Long roomId, HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        List<ChatMessageResponse> messages = chatService.getChatMessages(memberId, roomId);
        return ResponseEntity.ok(ApiUtil.success(messages));
    }


    /**
     * 메시지 읽음 처리
     * POST /api/chat/messages/read
     */
    /*
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping("/read")
    public ResponseEntity<?> markAsRead(@RequestBody MarkAsReadRequest request) {
        chatService.markAsRead(
                request.getChatRoomId(),
                request.getMemberId(),
                request.getLastMessageId()
        );
        return ResponseEntity.ok(ApiUtil.success("메시지를 읽음 처리했습니다."));
    }
    */


    /**
     * 안읽은 메시지 개수 조회
     * GET /api/chat/messages/unread-count?chatRoomId=1&memberId=1
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @RequestParam Long chatRoomId,
            @RequestParam Long memberId) {
        int count = chatService.getUnreadCount(chatRoomId, memberId);
        return ResponseEntity.ok(ApiUtil.success(count));
    }


    /**
     * 이미지 메시지 전송
     * 서버가 자동으로 WebSocket 브로드캐스트
     * POST /api/chat/messages/image
     */
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping("/{roomId}/image")
    public ResponseEntity<?> sendImageMessage(
            HttpServletRequest request,
            @PathVariable(name = "roomId") Long roomId,
            @RequestParam("image") MultipartFile image) {
        Long memberId = (Long) request.getAttribute("memberId");
        ChatMessageResponse response = chatService.sendImageMessage(roomId, memberId, image);
        return ResponseEntity.ok(ApiUtil.success(response));
    }


    /**
     * 이미지 다운로드
     * GET /api/chat/messages/image/{messageId}
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/image/{messageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long messageId) {
        try {
            byte[] imageData = chatService.getImageData(messageId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);  // 또는 IMAGE_PNG
            headers.setContentLength(imageData.length);

            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
