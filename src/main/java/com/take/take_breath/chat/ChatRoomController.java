package com.take.take_breath.chat;


import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath.chat.dto.ChatRoomListResponse;
import com.take.take_breath.chat.dto.CreateChatRoomRequest;
import com.take.take_breath.chat.dto.CreateChatRoomResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    /**
     * 내가 속한 채팅방 목록 조회
     * GET /api/chat/rooms
     */
    /*
        응답
        {
          "success": true,
          "response": [
            {
              "roomId": 1,
              "roomName": "사용자1 - 상담사A 채팅",
              "unreadCount": 5,
              "lastMessage": "안녕하세요. 상담 도와드리겠습니다.",
              "lastMessageTime": "2025-10-24T14:30:00",
              "otherMemberId": 2,
              "otherMemberName": "상담사A"
            },
            {
              "roomId": 3,
              "roomName": "사용자1 - 상담사B 채팅",
              "unreadCount": 0,
              "lastMessage": "감사합니다.",
              "lastMessageTime": "2025-10-23T10:15:00",
              "otherMemberId": 4,
              "otherMemberName": "상담사B"
            }
          ],
          "error": null
        }
    */
    @GetMapping
    public ResponseEntity<?> getRooms(HttpServletRequest request) {
        String memberEmail = request.getAttribute("memberEmail").toString();
        List<ChatRoomListResponse> rooms = chatService.getMyChatRoomsByEmail(memberEmail);

        // List<ChatRoomListResponse> rooms = chatService.getMyChatRooms(memberId);
        return ResponseEntity.ok(ApiUtil.success(rooms));
    }

    /**
     * 채팅방 생성 및 멤버 추가
     * POST /api/chat/rooms
     */
    /*
        요청
        {
          "roomName": "사용자1 - 상담사A 채팅",
          "memberIds": [1, 2]
        }
        또는
        {
          "memberIds": [1, 2]
        }
     */
    /*
        응답 - 기존 채팅방을 반환하는 응답도 아래와 동일함
        {
          "success": true,
          "response": {
            "roomId": 5,
            "roomName": "김철수 - 이영희",
            "roomType": "PRIVATE",
            "createdAt": "2025-10-24T15:30:00",
            "members": [
              {
                "memberId": 1,
                "memberName": "김철수",
                "email": "user1@example.com"
              },
              {
                "memberId": 2,
                "memberName": "이영희",
                "email": "counselor1@example.com"
              }
            ]
          },
          "error": null
        }
     */
    @PostMapping
    public ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomRequest request) {
        CreateChatRoomResponse response = chatService.createChatRoom(request);
        return ResponseEntity.ok(ApiUtil.success(response));
    }
}
