package com.take.take_breath.chat;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath.chat.member.Member;
import com.take.take_breath.chat.member.MemberRepository;
import com.take.take_breath.chat.message.*;
import com.take.take_breath.chat.room.ChatRoom;
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
    private final MemberRepository memberRepository;

    // 방 생성
    public ChatRoom createRoom(String name) {
        ChatRoom room = ChatRoom.builder()
                .name(name)
                .roomType(RoomType.PRIVATE)
                .build();

        return chatRoomRepository.save(room);
    }

    // 방 찾기
    public ChatRoom findRoomById(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다."));
    }

    // 방 목록 조회
    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
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
    public List<ChatMessage> getMessages(Long roomId) {
        ChatRoom room = findRoomById(roomId);
        return chatMessageRepository.findByChatRoom(room);
    }

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

    /*
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