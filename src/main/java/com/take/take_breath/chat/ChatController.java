package com.take.take_breath.chat;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.ApiUtil.ApiResult;
import com.take.take_breath.chat.message.ChatMessage;
import com.take.take_breath.chat.message.ChatMessageDto;
import com.take.take_breath.chat.message.ChatMessageDto.ChatMessageResponse;
import com.take.take_breath.chat.room.ChatRoom;
import com.take.take_breath.chat.room.ChatRoomDto;
import com.take.take_breath.chat.room.ChatRoomDto.ChatRoomRequest;
import com.take.take_breath.chat.room.ChatRoomDto.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 채팅방 생성
    @PostMapping("/room")
    public ApiResult<ChatRoomResponse> createRoom(@RequestBody ChatRoomRequest request) {
        System.out.println("채팅방 생성 요청");
        ChatRoom room = chatService.createRoom(request.getName());
        return ApiUtil.success(ChatRoomResponse.fromEntity(room));
    }

    // 채팅방 목록 조회
    @GetMapping("/rooms")
    public ApiUtil.ApiResult<List<ChatRoomResponse>> getRooms() {
        System.out.println("채팅방 목록 조회 요청");
        List<ChatRoom> rooms = chatService.getAllRooms();
        List<ChatRoomResponse> response = rooms.stream()
                .map(ChatRoomResponse::fromEntity)
                .toList();
        return ApiUtil.success(response);
    }

    // 특정 방 메세지 목록 조회
    @GetMapping("/room/{roomId}/messages")
    public ApiUtil.ApiResult<List<ChatMessageResponse>> getMessages(@PathVariable Long roomId) {
        List<ChatMessage> messages = chatService.getMessages(roomId);
        List<ChatMessageResponse> responseList = messages.stream()
                .map(ChatMessageResponse::fromEntity)
                .toList();
        return ApiUtil.success(responseList);
    }

    /**
     * 이미지 메세지 전송
     * POST http://localhost:8080/api/chat/room/1/image
     * @param roomId
     * @param senderId
     * @param imageFile
     * @return
     */
    @PostMapping("/room/{roomId}/image")
    public ApiResult<ChatMessageResponse> uploadImage(
            @PathVariable Long roomId,
            @RequestParam Long senderId,
            @RequestParam("file") MultipartFile imageFile
    ) {
        try {
            ChatMessage chat = chatService.saveImageMessage(roomId, senderId, imageFile);
            ChatMessageResponse response = ChatMessageResponse.fromEntity(chat);

            // 웹소켓 브로드캐스트
            messagingTemplate.convertAndSend("/topic/room." + roomId, ApiUtil.success(response));

            return ApiUtil.success(response);
        } catch (Exception e) {
            return ApiUtil.fail("이미지 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 파일 메세지 전송
     * POST http://localhost:8080/api/chat/room/1/file
     */
    @PostMapping("/room/{roomId}/file")
    public ApiResult<ChatMessageResponse> uploadFile(
            @PathVariable Long roomId,
            @RequestParam Long senderId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            ChatMessage chat = chatService.saveImageMessage(roomId, senderId, file);
            ChatMessageResponse response = ChatMessageResponse.fromEntity(chat);

            // 웹소켓 브로드캐스트
            messagingTemplate.convertAndSend("/topic/room." + roomId, ApiUtil.success(response));

            return ApiUtil.success(response);
        } catch (Exception e) {
            return ApiUtil.fail("파일 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

/*
레거시 - mk1


@Controller
public class ChatController {
    // 클라 -> 서버 : /app/chat.sendMessage
    // 서버 -> 클라 : /topic/public
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage message) {
        System.out.println("받은 메세지: " + message.getContent());

        // 임시 ChatRoom 객체 - DB 저장 X
        ChatRoom room = new ChatRoom("room1");

        // 받은 메세지를 ChatMessage 엔티티 형태로 반환
        ChatMessage entityMessage = new ChatMessage(
                message.getSender(),
                message.getContent(),
                room
        );

        return message;
    }
}

 */

/*

@Controller
public class ChatController {

    // 임시 메모리 저장 (나중에 Repository로 대체)
    private Map<String, ChatRoom> rooms = new HashMap<>();

    // 채팅방 생성
    @PostMapping("/api/chat/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam String name) {
        ChatRoom room = new ChatRoom(name);
        rooms.put(name, room);
        return room;
    }

    // 2. 채팅방 입장 + 메세지 보내기
    // 클라 -> 서버 : /app/chat.sendMessage.{roomName}
    // 서버 -> 클라 : /topic/room.{roomName}
    @MessageMapping("/chat.sendMessage.{roomName}")
    @SendTo("/topic/room.{roomName}")
    public ChatMessage sendMessage(@DestinationVariable String roomName, ChatMessage message) {
        System.out.println("방 [" + roomName + "] 메세지 : " + message.getContent());
        ChatRoom room = rooms.get(roomName);

        if(room == null) {
            room = new ChatRoom(roomName);
            rooms.put(roomName, room);
        }

        ChatMessage chatMessage = new ChatMessage(
                message.getSender(),
                message.getContent(),
                room
        );

        return ApiUtil.WsApiResult.success("CHAT", chatMessage);
    }
}

 */

/*

@Controller
@RequiredArgsConstructor
public class ChatController {

    // 임시 메모리 저장 (나중에 Repository로 대체)
    private Map<String, ChatRoom> rooms = new HashMap<>();

    // 채팅방 생성
    @PostMapping("/api/chat/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam String name) {
        ChatRoom room = new ChatRoom(name);
        rooms.put(name, room);
        return room;
    }

    // 2. 채팅방 입장 + 메세지 보내기
    // 클라 -> 서버 : /app/chat.sendMessage.{roomName}
    // 서버 -> 클라 : /topic/room.{roomName}
    @MessageMapping("/chat.sendMessage.{roomName}")
    @SendTo("/topic/room.{roomName}")
    public WsApiUtil.WsApiResult<ChatMessage> sendMessage(@DestinationVariable String roomName, ChatMessage message) {
        System.out.println("방 [" + roomName + "] 메세지 : " + message.getContent());
        ChatRoom room = rooms.get(roomName);

        if (room == null) {
            room = new ChatRoom(roomName);
            rooms.put(roomName, room);
        }

        ChatMessage chatMessage = new ChatMessage(
                message.getSender(),
                message.getContent(),
                room
        );

        return WsApiUtil.success("CHAT", chatMessage);
    }
}

 */