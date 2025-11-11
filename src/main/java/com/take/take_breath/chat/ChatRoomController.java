package com.take.take_breath.chat;


import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil.SliceResponse;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.chat.dto.ChatRoomListResponse;
import com.take.take_breath.chat.dto.CreateChatRoomRequest;
import com.take.take_breath.chat.dto.CreateChatRoomResponse;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    /**
     * 내가 속한 채팅방 목록 조회
     * GET /api/chat/rooms
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping
    public ResponseEntity<?> getRooms(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        System.out.println("채팅창 부름");
        String memberEmail = request.getAttribute("memberEmail").toString();
        Long memberId = (Long) request.getAttribute("memberId");

        SliceResponse<ChatRoomListResponse> response
                = chatService.getMyChatRoomsToSlice(memberId, page, size);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 채팅방 생성 및 멤버 추가
     * POST /api/chat/rooms
     */
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping
    public ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomRequest request) {
        CreateChatRoomResponse response = chatService.createChatRoom(request);
        return ResponseEntity.ok(ApiUtil.success(response));
    }
}

/*
@GetMapping
    public ResponseEntity<?> getRooms(HttpServletRequest request) {
        String memberEmail = request.getAttribute("memberEmail").toString();
        List<ChatRoomListResponse> rooms = chatService.getMyChatRoomsByEmail(memberEmail);

        // List<ChatRoomListResponse> rooms = chatService.getMyChatRooms(memberId);
        return ResponseEntity.ok(ApiUtil.success(rooms));
    }
 */