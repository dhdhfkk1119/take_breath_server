package com.take.take_breath.chat;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath.chat.member.Member;
import com.take.take_breath.chat.member.MemberRepository;
import com.take.take_breath.chat.message.ChatMessage;
import com.take.take_breath.chat.message.ChatMessageDto;
import com.take.take_breath.chat.message.ChatMessageRepository;
import com.take.take_breath.chat.room.ChatRoom;
import com.take.take_breath.chat.room.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    // 방 생성
    public ChatRoom createRoom(String name) {
        ChatRoom room = ChatRoom.builder()
                .name(name)
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
                .build();

        // 메세지 저장 및 반환
        return chatMessageRepository.save(message);
    }

    // 특정 방의 모든 메세지 조회
    public List<ChatMessage> getMessages(Long roomId) {
        ChatRoom room = findRoomById(roomId);
        return chatMessageRepository.findByChatRoom(room);
    }
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