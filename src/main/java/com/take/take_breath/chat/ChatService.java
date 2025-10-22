package com.take.take_breath.chat;

import com.take.take_breath.chat.member.Member;
import com.take.take_breath.chat.member.MemberRepository;
import com.take.take_breath.chat.message.ChatMessage;
import com.take.take_breath.chat.message.ChatMessageRepository;
import com.take.take_breath.chat.room.ChatRoom;
import com.take.take_breath.chat.room.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    // 방 생성
    public ChatRoom createRoom(String name) {
        return chatRoomRepository.save(ChatRoom.builder()
                .name(name)
                .build()
        );
    }

    // 방 찾기
    public ChatRoom findRoomById(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
    }

    // 메세지 저장
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

    // 특정 방의 모든 메세지 조회
    public List<ChatMessage> getMessages(Long roomId) {
        ChatRoom room = findRoomById(roomId);
        return chatMessageRepository.findByChatRoom(room);
    }
}
