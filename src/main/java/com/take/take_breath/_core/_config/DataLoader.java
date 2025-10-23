package com.take.take_breath._core._config;

import com.take.take_breath.chat.member.Member;
import com.take.take_breath.chat.member.MemberRepository;
import com.take.take_breath.chat.member.Role;
import com.take.take_breath.chat.member.Status;
import com.take.take_breath.chat.message.ChatMessage;
import com.take.take_breath.chat.message.ChatMessageRepository;
import com.take.take_breath.chat.message.MessageStatus;
import com.take.take_breath.chat.message.MessageType;
import com.take.take_breath.chat.room.ChatRoom;
import com.take.take_breath.chat.room.ChatRoomRepository;
import com.take.take_breath.chat.room.RoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("서버 실행");

        // 유저 생성
        // ✅ 유저 생성
        Member user1 = memberRepository.save(Member.builder()
                .email("test1@test.com")
                .password("1234") // 실제론 암호화해야 함 (예시니까 plain text)
                .name("테스트유저1")
                .phone("010-1111-1111")
                .address("Seoul")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build());

        Member user2 = memberRepository.save(Member.builder()
                .email("test2@test.com")
                .password("1234")
                .name("테스트유저2")
                .phone("010-2222-2222")
                .address("Busan")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build());

        Member user3 = memberRepository.save(Member.builder()
                .email("test3@test.com")
                .password("1234")
                .name("테스트유저3")
                .phone("010-3333-3333")
                .address("Incheon")
                .role(Role.COUNSELOR)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .certificationUrl("http://example.com/cert/bob")
                .build());

        // 채팅방 생성
        ChatRoom room1 = chatRoomRepository.save(
                ChatRoom.builder()
                        .name("test_room1")
                        .roomType(RoomType.PRIVATE)
                        .build()
        );
        ChatRoom room2 = chatRoomRepository.save(
                ChatRoom.builder()
                        .name("test_room2")
                        .roomType(RoomType.PRIVATE)
                        .build()
        );

        // 메세지 추가
        chatMessageRepository.save(ChatMessage.builder()
                .sender(user1)
                .content("안녕하세요, room1 첫 메시지입니다.")
                .chatRoom(room1)
                .type(MessageType.TEXT)
                .status(MessageStatus.SENT)
                .build());

        chatMessageRepository.save(ChatMessage.builder()
                .sender(user2)
                .content("반가워요, room1 두 번째 메시지입니다.")
                .chatRoom(room1)
                .type(MessageType.TEXT)
                .status(MessageStatus.SENT)
                .build());

        chatMessageRepository.save(ChatMessage.builder()
                .sender(user3)
                .content("room2의 첫 대화입니다.")
                .chatRoom(room2)
                .type(MessageType.TEXT)
                .status(MessageStatus.SENT)
                .build());

        System.out.println("초기 데이터 삽입 완료");
    }
}
