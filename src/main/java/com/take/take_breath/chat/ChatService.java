package com.take.take_breath.chat;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath._core._utils.UploadFile;
import com.take.take_breath._core._utils.UploadProperties;
import com.take.take_breath.chat.chat_message.ChatMessage;
import com.take.take_breath.chat.chat_message.ChatMessageRepository;
import com.take.take_breath.chat.chat_message.MessageType;
import com.take.take_breath.chat.chat_room.ChatRoom;
import com.take.take_breath.chat.chat_room.ChatRoomRepository;
import com.take.take_breath.chat.chat_room.RoomType;
import com.take.take_breath.chat.chat_room_member.ChatRoomMember;
import com.take.take_breath.chat.chat_room_member.ChatRoomMemberRepository;
import com.take.take_breath.chat.dto.*;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UploadFile uploadFile;
    private final UploadProperties uploadProperties;


    /**
     * 채팅방의 메시지 목록 조회 (읽음 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatMessages(Long chatRoomId, Long memberId) {
        // 예외 처리
        memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));
        chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다"));

        // 채팅방의 모든 메시지 조회 (시간순 정렬)
        List<ChatMessage> messages = chatMessageRepository
                .findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);

        // 2. 현재 사용자의 마지막 읽은 메시지 ID 조회
        ChatRoomMember roomMember = chatRoomMemberRepository
                .findByChatRoomIdAndMemberId(chatRoomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 속하지 않은 사용자입니다."));

        Long lastReadMessageId = roomMember.getLastReadMessageId();

        // 3. Entity -> DTO 변환 (읽음 여부 계산)
        return messages.stream()
                .map(message -> {
                    // 읽음 여부 판단:
                    // 1. 본인이 보낸 메시지거나
                    // 2. lastReadMessageId보다 작거나 같으면 읽음 처리
                    boolean isRead = message.getSender().getId().equals(memberId) ||
                            (lastReadMessageId != null && message.getId() <= lastReadMessageId);

                    return ChatMessageResponse.builder()
                            .messageId(message.getId())
                            .senderId(message.getSender().getId())
                            .senderName(message.getSender().getName())
                            .content(message.getContent())
                            .messageType(message.getType().name())
                            .createdAt(message.getTime())
                            .isRead(isRead)
                            .imageUrl(message.getImageUrl())  // ⭐ 이미지 URL 추가
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 메시지 읽음 처리
     * - 사용자가 채팅방에서 메시지를 읽었을 때 호출
     * - lastReadMessageId를 업데이트
     */
    public void markAsRead(Long chatRoomId, Long memberId, Long lastMessageId) {
        // 채팅방 멤버 조회
        ChatRoomMember roomMember = chatRoomMemberRepository
                .findByChatRoomIdAndMemberId(chatRoomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 속하지 않은 사용자입니다."));

        // 기존 lastReadMessageId보다 큰 경우만 업데이트
        //    (이전 메시지를 다시 읽었다고 lastReadMessageId를 낮추지 않기 위함)
        if (roomMember.getLastReadMessageId() == null || lastMessageId > roomMember.getLastReadMessageId()) {
            roomMember.setLastReadMessageId(lastMessageId);
            roomMember.setLastReadAt(LocalDateTime.now());
            chatRoomMemberRepository.save(roomMember);
        }
    }

    /**
     * 안읽은 메시지 개수 조회
     */
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long chatRoomId, Long memberId) {
        ChatRoomMember roomMember = chatRoomMemberRepository
                .findByChatRoomIdAndMemberId(chatRoomId, memberId)
                .orElseThrow(() -> new Exception400("채팅방에 속하지 않은 사용자입니다."));

        return chatMessageRepository.countUnreadMessages(
                chatRoomId,
                roomMember.getLastReadMessageId(),
                memberId
        );
    }

    /**
     * 텍스트 메시지 전송
     */
    public ChatMessage sendMessage(ChatMessageRequest request) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 발신자 조회
        Member sender = memberRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // MessageType 기본값 처리
        String messageTypeStr = request.getMessageType();
        if (messageTypeStr == null || messageTypeStr.trim().isEmpty()) {
            messageTypeStr = "TEXT";
        }

        // 메시지 생성 및 저장
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .type(MessageType.valueOf(messageTypeStr))
                .build();
        chatMessageRepository.save(message);

        // 발신자는 자동으로 읽음 처리
        markAsRead(request.getChatRoomId(), request.getSenderId(), message.getId());

        return message;
    }

    /**
     * 이미지 메시지 전송
     */
    public ImageMessageResponse sendImageMessage(ImageUploadRequest request) throws IOException {
        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 2. 발신자 조회
        Member sender = memberRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 3. 이미지 파일 검증
        MultipartFile image = request.getImage();
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 없습니다.");
        }

        // 4. 이미지 파일 크기 제한 (예: 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (image.getSize() > maxSize) {
            throw new IllegalArgumentException("이미지 파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // 5. 이미지 파일 형식 검증
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        // 6. UploadFile을 사용해서 파일 저장
        String attachmentPath = uploadFile.uploadImage(image, "chat");

        // 7. 메시지 생성 및 저장
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content("[이미지]")
                .type(MessageType.IMAGE)
                .attachmentPath(attachmentPath)
                .originalFilename(image.getOriginalFilename())
                .fileSize(image.getSize())
                .build();
        chatMessageRepository.save(message);

        // 8. 발신자는 자동으로 읽음 처리
        markAsRead(request.getChatRoomId(), request.getSenderId(), message.getId());

        // 9. 웹소켓으로 자동 브로드캐스트
        ImageMessageResponse response = ImageMessageResponse.builder()
                .messageId(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .messageType(message.getType().name())
                .imageUrl(message.getImageUrl())  // "/api/chat/messages/image/{id}"
                .createdAt(message.getTime())
                .isRead(true)
                .build();

        messagingTemplate.convertAndSend(
                "/topic/room." + request.getChatRoomId(),
                response
        );

        return response;
    }

    /**
     * 이미지 데이터 조회
     */
    public byte[] getImageData(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        if (message.getType() != MessageType.IMAGE) {
            throw new IllegalArgumentException("이미지 메시지가 아닙니다.");
        }

        if (message.getAttachmentPath() == null || message.getAttachmentPath().isEmpty()) {
            throw new IllegalArgumentException("이미지 파일 경로가 없습니다.");
        }

        // ⭐ UploadFile을 사용해서 파일 읽기
        try {
            return uploadFile.loadChatImage(message.getAttachmentPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 내가 속한 채팅방 목록 조회 (읽지 않은 메시지 개수 포함)
     */
    @Transactional(readOnly = true)
    public List<ChatRoomListResponse> getMyChatRooms(Long memberId) {
        // 예외 처리
        memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));

        // 내가 속한 채팅방 목록 조회
        List<ChatRoomMember> myRoomMembers = chatRoomMemberRepository
                .findByMemberId(memberId);

        // Entity -> DTO 변환
        return myRoomMembers.stream()
                .map(myRoomMember -> {
                    Long roomId = myRoomMember.getChatRoom().getId();

                    // 읽지 않은 메시지 개수 계산
                    Long unreadCount = chatMessageRepository.countUnreadMessages(
                            roomId,
                            myRoomMember.getLastReadMessageId(), // 내가 마지막으로 읽은 메시지 ID
                            memberId  // 내 ID (내가 보낸 메시지는 제외)
                    );

                    // 마지막 메시지 조회
                    ChatMessage lastMessage = chatMessageRepository
                            .findLastMessageByChatRoomId(roomId);

                    // 1:1 채팅이므로 상대방 정보 조회
                    ChatRoomMember otherMember = chatRoomMemberRepository
                            .findOtherMemberInRoom(roomId, memberId);

                    return ChatRoomListResponse.builder()
                            .roomId(roomId)
                            .roomName(myRoomMember.getChatRoom().getName())
                            .unreadCount(unreadCount)
                            .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                            .lastMessageTime(lastMessage != null ? lastMessage.getTime() : null)
                            .otherMemberId(otherMember != null ? otherMember.getMember().getId() : null)
                            .otherMemberName(otherMember != null ? otherMember.getMember().getName() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 생성 및 멤버 추가
     */
    public CreateChatRoomResponse createChatRoom(CreateChatRoomRequest request) {
        // 1. 유효성 검증
        if (request.getMemberIds() == null || request.getMemberIds().isEmpty()) {
            throw new IllegalArgumentException("참여할 회원이 없습니다.");
        }

        // 1:1 채팅이므로 2명만 허용
        if (request.getMemberIds().size() != 2) {
            throw new IllegalArgumentException("1:1 채팅은 2명만 참여 가능합니다.");
        }

        // 2. 회원 존재 여부 확인
        List<Member> members = memberRepository.findAllById(request.getMemberIds());
        if (members.size() != request.getMemberIds().size()) {
            throw new IllegalArgumentException("존재하지 않는 회원이 포함되어 있습니다.");
        }

        // 3. 이미 두 회원 간의 채팅방이 있는지 확인 (중복 방지)
        ChatRoom existingRoom = chatRoomMemberRepository
                .findExistingPrivateRoom(request.getMemberIds().get(0), request.getMemberIds().get(1));
        if (existingRoom != null) {
            // 이미 존재하는 방이 있으면 그 방 정보 반환
            return buildChatRoomResponse(existingRoom, members);
        }

        // 4. 채팅방 이름 생성 (입력이 없으면 자동 생성)
        String roomName = request.getRoomName();
        if (roomName == null || roomName.trim().isEmpty()) {
            roomName = members.stream()
                    .map(Member::getName)
                    .collect(Collectors.joining(" - "));
        }

        // 5. 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .roomType(RoomType.PRIVATE)  // 1:1 채팅
                .build();
        chatRoomRepository.save(chatRoom);

        // 6. 채팅방에 멤버 추가
        for (Member member : members) {
            ChatRoomMember roomMember = ChatRoomMember.builder()
                    .chatRoom(chatRoom)
                    .member(member)
                    .lastReadMessageId(null)  // 초기값 null
                    .build();
            chatRoomMemberRepository.save(roomMember);
        }

        // 7. 응답 생성
        return buildChatRoomResponse(chatRoom, members);
    }

    /**
     * ChatRoom과 Members를 CreateChatRoomResponse로 변환
     */
    private CreateChatRoomResponse buildChatRoomResponse(ChatRoom chatRoom, List<Member> members) {
        List<CreateChatRoomResponse.MemberInfo> memberInfos = members.stream()
                .map(member -> CreateChatRoomResponse.MemberInfo.builder()
                        .memberId(member.getId())
                        .memberName(member.getName())
                        .email(member.getEmail())
                        .build())
                .collect(Collectors.toList());

        return CreateChatRoomResponse.builder()
                .roomId(chatRoom.getId())
                .roomName(chatRoom.getName())
                .roomType(chatRoom.getRoomType().name())
                .createdAt(chatRoom.getCreatedAt())
                .members(memberInfos)
                .build();
    }

}

