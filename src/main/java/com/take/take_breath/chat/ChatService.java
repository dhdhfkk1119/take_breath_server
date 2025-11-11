package com.take.take_breath.chat;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath._core._exception.Exception500;
import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath._core._utils.PageUtil.SliceResponse;
import com.take.take_breath._core._utils.UploadFile;
import com.take.take_breath.chat.chat_message.ChatMessage;
import com.take.take_breath.chat.chat_message.ChatMessageRepository;
import com.take.take_breath.chat.chat_message.MessageType;
import com.take.take_breath.chat.chat_room.ChatRoom;
import com.take.take_breath.chat.chat_room.ChatRoomRepository;
import com.take.take_breath.chat.chat_room.RoomType;
import com.take.take_breath.chat.chat_room_member.ChatRoomMember;
import com.take.take_breath.chat.chat_room_member.ChatRoomMemberRepository;
import com.take.take_breath.chat.dto.*;
import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    private static final int REQUIRED_POINT = 500;

    /**
     * 채팅방의 메시지 목록 조회 (읽음 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatMessages(Long memberId, Long roomId) {
        // 예외 처리
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다"));

        // 채팅방의 모든 메시지 조회 (시간순 정렬)
        List<ChatMessage> messages = chatMessageRepository
                .findByChatRoomIdOrderByCreatedAtAsc(roomId);

        // 현재 사용자의 마지막 읽은 메시지 ID 조회
        ChatRoomMember roomMember = chatRoomMemberRepository
                .findByChatRoomIdAndMemberId(room.getId(), member.getId())
                .orElseThrow(() -> new Exception400("채팅방에 속하지 않은 사용자입니다."));

        Long lastReadMessageId = roomMember.getLastReadMessageId();

        // Entity -> DTO 변환 (읽음 여부 계산)
        return messages.stream()
                .map(message -> {
                    // 읽음 여부 판단:
                    // 1. 본인이 보낸 메시지거나
                    // 2. lastReadMessageId보다 작거나 같으면 읽음 처리
                    boolean isRead = message.getSender().getId().equals(member.getId()) ||
                            (lastReadMessageId != null && message.getId() <= lastReadMessageId);

                    return ChatMessageResponse.builder()
                            .messageId(message.getId())
                            .senderId(message.getSender().getId())
                            .senderName(message.getSender().getName())
                            .content(message.getContent())
                            .messageType(message.getType())
                            .createdAt(message.getCreatedAt())
                            .isRead(isRead)
                            .attachmentPath(message.getAttachmentPath())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 텍스트 메시지 전송
     */
    public ChatMessageResponse sendMessage(Long roomId, Long senderId, ChatMessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다."));
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new Exception404("회원을 찾을 수 없습니다."));

        Long currentPoint = null;
        if (sender.getRole() == Role.USER) {
            if (sender.getPoint() < REQUIRED_POINT) {
                throw new Exception400("포인트가 부족합니다. 현재 포인트: " + sender.getPoint());
            }

            // 포인트 차감
            sender.setPoint(sender.getPoint() - REQUIRED_POINT);
            memberRepository.save(sender);
            currentPoint = sender.getPoint();  // 차감 후 포인트

            // 상담사 포인트 적립
            ChatRoomMember otherMember = chatRoomMemberRepository
                    .findOtherMemberInRoom(roomId, senderId);

            if (otherMember != null && otherMember.getMember().getRole() == Role.COUNSELOR) {
                Counselor counselor = otherMember.getMember().getCounselor();
                if (counselor != null) {
                    counselor.setPoint(counselor.getPoint() + REQUIRED_POINT);
                }
            }
        }

        // 메시지 타입 처리
        MessageType messageType = request.getMessageType() != null ? request.getMessageType() : MessageType.TEXT;

        // 메시지 생성 및 저장
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .type(messageType)
                .build();
        chatMessageRepository.save(message);

        // 발신자는 읽음 처리
        markAsRead(roomId, senderId, message.getId());

        // Entity -> DTO 변환
        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .senderId(sender.getId())
                .senderName(sender.getName())
                .content(message.getContent())
                .messageType(message.getType())
                .createdAt(message.getCreatedAt())
                .isRead(true)
                .currentPoint(currentPoint)
                .build();
    }

    /**
     * 이미지 메시지 전송
     */
    public ChatMessageResponse sendImageMessage(
            Long roomId,
            Long senderId,
            MultipartFile image) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다."));
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new Exception404("회원을 찾을 수 없습니다."));

        Long currentPoint = null;
        if (sender.getRole() == Role.USER) {
            if (sender.getPoint() < REQUIRED_POINT) {
                throw new Exception400("포인트가 부족합니다. 현재 포인트: " + sender.getPoint());
            }

            // 포인트 차감
            sender.setPoint(sender.getPoint() - REQUIRED_POINT);
            memberRepository.save(sender);
            currentPoint = sender.getPoint();  // 차감 후 포인트

            // 상담사 포인트 적립
            ChatRoomMember otherMember = chatRoomMemberRepository
                    .findOtherMemberInRoom(roomId, senderId);

            if (otherMember != null && otherMember.getMember().getRole() == Role.COUNSELOR) {
                Counselor counselor = otherMember.getMember().getCounselor();
                if (counselor != null) {
                    counselor.setPoint(counselor.getPoint() + REQUIRED_POINT);
                }
            }
        }

        // 이미지 파일 검증
        if (image == null || image.isEmpty()) {
            throw new Exception400("이미지 파일이 없습니다.");
        }

        // 이미지 파일 크기 제한 (예: 10MB)
        /*
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (image.getSize() > maxSize) {
            throw new Exception400("이미지 파일 크기는 10MB를 초과할 수 없습니다.");
        }
        */

        // 이미지 파일 형식 검증
        /*
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new Exception400("이미지 파일만 업로드 가능합니다.");
        }
        */

        // UploadFile을 사용해서 파일 저장
        String attachmentPath;
        try {
            String relativePath = uploadFile.uploadImage(image, "chat");
            attachmentPath = "/uploads/" + relativePath;
        } catch (IOException e) {
            throw new Exception500("이미지 업로드에 실패했습니다.");
        }

        // 메시지 생성 및 저장
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

        // 발신자는 자동으로 읽음 처리
        markAsRead(chatRoom.getId(), sender.getId(), message.getId());

        // 웹소켓으로 자동 브로드캐스트
        ChatMessageResponse response = ChatMessageResponse.builder()
                .messageId(message.getId())
                .senderId(senderId)
                .senderName(sender.getName())
                .content("[이미지]")
                .messageType(MessageType.IMAGE)
                .createdAt(message.getCreatedAt())
                .isRead(true)
                .attachmentPath(attachmentPath)
                .currentPoint(currentPoint)
                .build();

        // 웹소켓 브로드캐스트
        messagingTemplate.convertAndSend(
                "/sub/chat/room." + roomId,
                ApiUtil.success(response)
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
     * 안읽은 메시지 개수 조회
     */
    @Transactional(readOnly = true)
    public int getUnreadCount(Long chatRoomId, Long memberId) {
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
     * 메시지 읽음 처리
     * - 사용자가 채팅방에서 메시지를 읽었을 때 호출
     * - lastReadMessageId를 업데이트
     */
    public void markAsRead(Long chatRoomId, Long memberId, Long lastMessageId) {
        ChatRoomMember roomMember = chatRoomMemberRepository
                .findByChatRoomIdAndMemberId(chatRoomId, memberId)
                .orElseThrow(() -> new Exception400("채팅방에 속하지 않은 사용자입니다."));

        if (roomMember.getLastReadMessageId() == null || lastMessageId > roomMember.getLastReadMessageId()) {
            roomMember.updateLastRead(lastMessageId);
            chatRoomMemberRepository.save(roomMember);
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
                    int unreadCount = chatMessageRepository.countUnreadMessages(
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
     * 내가 속한 채팅방 목록 조회 (이메일로 조회)
     */
    @Transactional(readOnly = true)
    public SliceResponse<ChatRoomListResponse> getMyChatRoomsToSlice(Long memberId, int page, int size) {
        // 사용자 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        // Pageable 생성
        Pageable pageable = PageRequest.of(page, size);

        // Slice로 채팅방 조회
        Slice<ChatRoomMember> chatRoomMemberSlice = chatRoomMemberRepository
                .findByMemberIdOrderByLastMessageTime(member.getId(), pageable);

        List<ChatRoomListResponse> content = chatRoomMemberSlice.getContent().stream()
                .map(myRoomMember -> {
                    Long roomId = myRoomMember.getChatRoom().getId();

                    // 읽지 않은 메시지 개수 계산
                    int unreadCount = chatMessageRepository.countUnreadMessages(
                            roomId,
                            myRoomMember.getLastReadMessageId(),
                            member.getId()
                    );

                    // 마지막 메시지 조회
                    ChatMessage lastMessage = chatMessageRepository
                            .findLastMessageByChatRoomId(roomId);

                    // 상대방 정보 조회
                    ChatRoomMember otherMember = chatRoomMemberRepository
                            .findOtherMemberInRoom(roomId, member.getId());

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

        return SliceResponse.of(chatRoomMemberSlice, content);
    }

    /**
     * 채팅방 생성 및 멤버 추가
     */
    public CreateChatRoomResponse createChatRoom(CreateChatRoomRequest request) {
        // 1. 유효성 검증
        if (request.getMemberIds() == null || request.getMemberIds().isEmpty()) {
            throw new Exception400("참여할 회원이 없습니다.");
        }

        // 1:1 채팅이므로 2명만 허용
        if (request.getMemberIds().size() != 2) {
            throw new Exception400("1:1 채팅은 2명만 참여 가능합니다.");
        }

        // 2. 회원 존재 여부 확인
        List<Member> members = memberRepository.findAllById(request.getMemberIds());
        if (members.size() != request.getMemberIds().size()) {
            throw new Exception404("존재하지 않는 회원이 포함되어 있습니다.");
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
                .roomType(RoomType.COUNSELING)  // 1:1 채팅
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
                .members(memberInfos)
                .build();
    }
}

/*
@Transactional(readOnly = true)
    public List<ChatRoomListResponse> getMyChatRoomsByEmail(String email, int page, int size) {
        // 사용자 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        // Pageable 생성
        Pageable pageable = PageRequest.of(page, size);

        Slice<ChatRoomMember> chatRoomMemberSlice = chatRoomMemberRepository
                .findByMemberIdOrderByLastMessageTime(member.getId(), pageable);


        List<ChatRoomMember> myRoomMembers = chatRoomMemberRepository.findByMemberId(member.getId());

        // Entity -> DTO 변환
        return myRoomMembers.stream()
                .map(myRoomMember -> {
                    Long roomId = myRoomMember.getChatRoom().getId();

                    // 읽지 않은 메시지 개수 계산
                    int unreadCount = chatMessageRepository.countUnreadMessages(
                            roomId,
                            myRoomMember.getLastReadMessageId(), // 내가 마지막으로 읽은 메시지 ID
                            member.getId()  // 내 ID (내가 보낸 메시지는 제외)
                    );

                    // 마지막 메시지 조회
                    ChatMessage lastMessage = chatMessageRepository
                            .findLastMessageByChatRoomId(roomId);

                    // 1:1 채팅이므로 상대방 정보 조회
                    ChatRoomMember otherMember = chatRoomMemberRepository
                            .findOtherMemberInRoom(roomId, member.getId());

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
 */