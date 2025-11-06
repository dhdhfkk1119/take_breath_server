package com.take.take_breath.chat;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath.chat.dto.ChatMessageResponse;
import com.take.take_breath.chat.dto.ImageMessageResponse;
import com.take.take_breath.chat.dto.ImageUploadRequest;
import com.take.take_breath.chat.dto.MarkAsReadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/chat/messages")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatService chatService;

    /**
     * 에러 반환
     */
    /*
        응답
        {
          "success": false,
          "response": null,
          "error": {
            "message": "회원을 찾을 수 없습니다.",
            "status": 404,
            "code": "MEMBER_NOT_FOUND"
          }
        }
     */


    /**
     * 채팅방의 메시지 목록 조회 (읽음 여부 포함)
     * GET /api/chat/messages?chatRoomId=1&memberId=1
     */
    /*
        응답
        {
          "success": true,
          "response": [
            {
              "messageId": 1,
              "senderId": 1,
              "senderName": "사용자1",
              "content": "안녕하세요",
              "messageType": "TEXT",
              "createdAt": "2025-10-24T10:00:00",
              "isRead": true
            },
            {
              "messageId": 2,
              "senderId": 2,
              "senderName": "상담사A",
              "content": "네, 안녕하세요. 무엇을 도와드릴까요?",
              "messageType": "TEXT",
              "createdAt": "2025-10-24T10:01:00",
              "isRead": true
            },
            {
              "messageId": 3,
              "senderId": 2,
              "senderName": "상담사A",
              "content": "상담 가능한 시간대를 알려드리겠습니다.",
              "messageType": "TEXT",
              "createdAt": "2025-10-24T10:05:00",
              "isRead": false
            }
          ],
          "error": null
        }
    */
    @GetMapping
    public ResponseEntity<?> getMessages(
            @RequestParam Long chatRoomId,
            @RequestParam Long memberId) {
        List<ChatMessageResponse> messages = chatService.getChatMessages(chatRoomId, memberId);
        return ResponseEntity.ok(ApiUtil.success(messages));
    }


    /**
     * 메시지 읽음 처리
     * POST /api/chat/messages/read
     */
    /*
        성공
        {
          "success": true,
          "response": "메시지를 읽음 처리했습니다.",
          "error": null
        }
        
        실패
        {
          "success": false,
          "response": null,
          "error": {
            "message": "채팅방에 속하지 않은 사용자입니다.",
            "status": 400,
            "code": "NOT_MEMBER_OF_CHATROOM"
          }
        }
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
