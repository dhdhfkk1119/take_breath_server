package com.take.take_breath.chat;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath.chat.dto.*;
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
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getMessages(@PathVariable(name = "roomId") Long roomId, HttpServletRequest request) {
        String memberEmail = request.getAttribute("memberEmail").toString();
        List<ChatMessageResponse> messages = chatService.getChatMessages(memberEmail, roomId);
        return ResponseEntity.ok(ApiUtil.success(messages));
    }


    /**
     * 메시지 읽음 처리
     * POST /api/chat/messages/read
     */
    @PostMapping("/read")
    public ResponseEntity<?> markAsRead(@RequestBody MarkAsReadRequest request) {
        chatService.markAsRead(
                request.getChatRoomId(),
                request.getMemberId(),
                request.getLastMessageId()
        );
        return ResponseEntity.ok(ApiUtil.success("메시지를 읽음 처리했습니다."));
    }


    /**
     * 안읽은 메시지 개수 조회
     * GET /api/chat/messages/unread-count?chatRoomId=1&memberId=1
     */
    /*
        {
          "success": true,
          "response": 5,
          "error": null
        }
     */
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
    /*
        chatRoomId: 1
        senderId: 1
        image: [이미지 파일]
     */
    /*
        {
          "success": true,
          "response": {
            "messageId": 20,
            "senderId": 1,
            "senderName": "김철수",
            "messageType": "IMAGE",
            "imageUrl": "/api/chat/messages/image/20",
            "createdAt": "2025-10-24T16:00:00",
            "isRead": true
          },
          "error": null
        }
     */
    @PostMapping("/image")
    public ResponseEntity<?> sendImageMessage(
            @RequestParam Long chatRoomId,
            @RequestParam Long senderId,
            @RequestParam("image") MultipartFile image) {
        // 이미지 메시지 저장
        ImageUploadRequest request = ImageUploadRequest.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .image(image)
                .build();

        ImageMessageResponse response = chatService.sendImageMessage(request);
        return ResponseEntity.ok(ApiUtil.success(response));
    }


    /**
     * 이미지 다운로드
     * GET /api/chat/messages/image/{messageId}
     */
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
