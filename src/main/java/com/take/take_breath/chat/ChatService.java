package com.take.take_breath.chat;

import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath.chat.chat_room_member.ChatRoomMember;
import com.take.take_breath.chat.chat_room_member.ChatRoomMemberRepository;
import com.take.take_breath.chat.member.Member;
import com.take.take_breath.chat.member.MemberRepository;
import com.take.take_breath.chat.message.ChatMessage;
import com.take.take_breath.chat.message.ChatMessageRepository;
import com.take.take_breath.chat.message.MessageStatus;
import com.take.take_breath.chat.message.MessageType;
import com.take.take_breath.chat.room.ChatRoom;
import com.take.take_breath.chat.room.ChatRoomDto.ChatRoomResponse;
import com.take.take_breath.chat.room.ChatRoomRepository;
import com.take.take_breath.chat.room.RoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;

    // 채팅방 생성 - ChatRoomMember 없던 시절
    /*
    public ChatRoom createRoom(String name) {
        ChatRoom room = ChatRoom.builder()
                .name(name)
                .roomType(RoomType.PRIVATE)
                .build();

        return chatRoomRepository.save(room);
    }
    */

    // 채팅방 목록 조회 - chatRoomMember 업던 시절
    /*
    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }
    */

    // 특정 채팅방 조회 - chatRoomMember 업던 시절
    /*
    public ChatRoom findRoomById(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다."));
    }
    */

    // 채팅방 생성 -> 참여자 등록
    ChatRoom createRoom(String name, Long userId1, Long userId2) {
        // 채팅방 저장
        ChatRoom room = ChatRoom.builder()
                .roomType(RoomType.PRIVATE)
                .build();
        room = chatRoomRepository.save(room);

        // 사용자, 상담사를 채팅방 멤버로 등록
        Member user1 = memberRepository.findById(userId1)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));
        Member user2 = memberRepository.findById(userId2)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));

        // 사용자와 상담사를 채팅방에 등록
        ChatRoomMember crm1 = ChatRoomMember.builder()
                .chatRoom(room)
                .member(user1)
                .build();
        ChatRoomMember crm2 = ChatRoomMember.builder()
                .chatRoom(room)
                .member(user2)
                .build();
        chatRoomMemberRepository.save(crm1);
        chatRoomMemberRepository.save(crm2);

        return room;
    }

    // 채팅방 목록 조회 - 내가 참여한 채팅방 목록만 - dto로 반환
    public List<C> getMyRooms(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));

        List<ChatRoomMember> myRoomMembers = chatRoomMemberRepository.findByMember(member);

        return myRoomMembers.stream()
                .map(crm -> {
                    ChatRoom room = crm.getChatRoom();
                    int unreadCount = calculateUnreadCount(crm);

                    return ChatRoomResponse.builder()
                            .id(room.getId())
                            .name(room.getName())
                            .roomType(room.getRoomType().name())
                            .unreadCount(unreadCount)
                            .createdAt(room.getCreatedAt())
                            .build();
                })
                .toList();
    }




    // 메세지 저장
    public ChatMessage saveMessage(Long memberId, String content, Long chatRoomId) {
        // 예외 처리
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new Exception404("해당 채팅방을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        // 메세지 객체 생성
        ChatMessage message = ChatMessage.builder()
                .sender(member)
                .content(content)
                .chatRoom(room)
                .type(MessageType.TEXT)
                .status(MessageStatus.SENT)
                .build();

        // 메세지 저장 및 반환
        return chatMessageRepository.save(message);
    }

    // 특정 방의 모든 메세지 조회
    /*
    public List<ChatMessage> getMessages(Long roomId) {
        ChatRoom room = findRoomById(roomId);
        return chatMessageRepository.findByChatRoom(room);
    }
    */

    // 파일 형식 저장 (이미지 + 파일)
    public ChatMessage saveAttachmentMessage(Long roomId, Long senderId, MultipartFile file) throws IOException {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다"));
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new Exception404("보낸 사람을 찾을 수 없습니다"));

        MessageType type = file.getContentType() != null && file.getContentType().startsWith("image/")
                ? MessageType.IMAGE
                : MessageType.FILE;

        ChatMessage chat = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .type(type)
                .status(MessageStatus.SENT)
                .attachmentData(file.getBytes())
                .build();

        return chatMessageRepository.save(chat);
    }

    // ⭐ 안읽은 메시지 개수 계산
    private int calculateUnreadCount(ChatRoomMember crm) {
        Long lastReadId = crm.getLastReadMessageId();

        if (lastReadId == null) {
            // 한 번도 안 읽었으면 전체 메시지 개수
            return chatMessageRepository.countByChatRoom(crm.getChatRoom());
        }

        // lastReadMessageId보다 큰 ID를 가진 메시지 개수
        return chatMessageRepository.countByChatRoomAndIdGreaterThan(
                crm.getChatRoom(),
                lastReadId
        );
    }

    /**
     public ChatMessage saveImageMessage(
     Long roomId,
     Long senderId,
     MultipartFile imageFile
     ) throws IOException {
     // 예외 처리
     ChatRoom room = chatRoomRepository.findById(roomId)
     .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다"));
     Member sender = memberRepository.findById(senderId)
     .orElseThrow(() -> new Exception404("보낸 사람을 찾을 수 없습니다"));

     // 객체 변환
     ChatMessage chat = ChatMessage.builder()
     .chatRoom(room)
     .sender(sender)
     .type(MessageType.IMAGE)
     .status(MessageStatus.SENT)
     .attachmentData(imageFile.getBytes()) // 이미지 → byte[] 변환
     .build();

     // 저장
     return chatMessageRepository.save(chat);
     }

     public ChatMessage saveFileMessage(
     Long roomId,
     Long senderId,
     MultipartFile file
     ) throws IOException {
     // 예외 처리
     ChatRoom room = chatRoomRepository.findById(roomId)
     .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다"));
     Member sender = memberRepository.findById(senderId)
     .orElseThrow(() -> new Exception404("보낸 사람을 찾을 수 없습니다"));

     // 객체 변환
     ChatMessage chat = ChatMessage.builder()
     .chatRoom(room)
     .sender(sender)
     .type(MessageType.FILE)
     .status(MessageStatus.SENT)
     .attachmentData(file.getBytes()) // 파일 → byte[] 변환
     .build();

     // 저장
     return chatMessageRepository.save(chat);
     }
     */

}


// 메세지 저장
/*
public ChatMessage saveMessage(Long memberId, String content, ChatRoom room) {
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

    ChatMessage msg = ChatMessage.builder()
            .sender(member)
            .content(content)
            .chatRoom(room)
            .build();

    return chatMessageRepository.save(msg);
}
*/

// 채팅방 목록 조회
/*
public List<ChatRoom> getAllRooms() {
    return chatRoomRepository.findAll();
}
*/

/*


    // 채팅방 목록 조회 - 내가 참여한 채팅방 목록만 - dto로 반환
    public List<ChatRoomResponse> getMyRooms(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));

        List<ChatRoomMember> myRoomMembers = chatRoomMemberRepository.findByMember(member);

        return myRoomMembers.stream()
                .map(crm -> {
                    ChatRoom room = crm.getChatRoom();
                    int unreadCount = calculateUnreadCount(crm);

                    return ChatRoomResponse.builder()
                            .id(room.getId())
                            .name(room.getName())
                            .roomType(room.getRoomType().name())
                            .unreadCount(unreadCount)
                            .createdAt(room.getCreatedAt())
                            .build();
                })
                .toList();
    }

 */