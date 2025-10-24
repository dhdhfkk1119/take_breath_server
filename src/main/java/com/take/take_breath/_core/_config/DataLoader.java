package com.take.take_breath._core._config;

import com.take.take_breath.chat.chat_room_member.ChatRoomMember;
import com.take.take_breath.chat.chat_room_member.ChatRoomMemberRepository;
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
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        // 기존 데이터가 있으면 초기화하지 않음
        if (memberRepository.count() > 0) {
            System.out.println("데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        System.out.println("========================================");
        System.out.println("테스트 데이터 초기화 시작...");
        System.out.println("========================================");

        // 1. 회원 생성
        Member user1 = createUser(
                "user1@example.com",
                "password123",
                "김철수",
                "010-1111-2222",
                "서울시 강남구",
                Role.USER
        );

        Member user2 = createUser(
                "user2@example.com",
                "password123",
                "이영희",
                "010-3333-4444",
                "서울시 서초구",
                Role.USER
        );

        Member counselor1 = createUser(
                "counselor1@example.com",
                "password123",
                "박상담",
                "010-5555-6666",
                "서울시 강남구",
                Role.COUNSELOR
        );

        Member counselor2 = createUser(
                "counselor2@example.com",
                "password123",
                "최상담",
                "010-7777-8888",
                "서울시 송파구",
                Role.COUNSELOR
        );

        System.out.println("✅ 회원 4명 생성 완료");

        // 2. 채팅방 생성 및 멤버 추가
        ChatRoom room1 = createChatRoom("김철수 - 박상담", user1, counselor1);
        ChatRoom room2 = createChatRoom("이영희 - 박상담", user2, counselor1);
        ChatRoom room3 = createChatRoom("김철수 - 최상담", user1, counselor2);

        System.out.println("✅ 채팅방 3개 생성 완료");

        // 3. 채팅방1에 메시지 추가 (김철수 - 박상담)
        ChatMessage msg1 = createMessage(room1, user1, "안녕하세요. 상담 신청합니다.", MessageType.TEXT);
        ChatMessage msg2 = createMessage(room1, counselor1, "네, 안녕하세요. 무엇을 도와드릴까요?", MessageType.TEXT);
        ChatMessage msg3 = createMessage(room1, user1, "요즘 스트레스가 심해서 상담받고 싶습니다.", MessageType.TEXT);
        ChatMessage msg4 = createMessage(room1, counselor1, "자세히 말씀해주시겠어요?", MessageType.TEXT);
        ChatMessage msg5 = createMessage(room1, user1, "직장에서 업무 압박이 심합니다.", MessageType.TEXT);

        // 김철수는 msg3까지 읽음 처리
        updateLastReadMessage(room1, user1, msg3.getId());
        // 박상담은 msg5까지 읽음 처리
        updateLastReadMessage(room1, counselor1, msg5.getId());

        System.out.println("✅ 채팅방1에 메시지 5개 추가 (김철수 안읽음 2개)");

        // 4. 채팅방2에 메시지 추가 (이영희 - 박상담)
        ChatMessage msg6 = createMessage(room2, user2, "상담 예약하고 싶습니다.", MessageType.TEXT);
        ChatMessage msg7 = createMessage(room2, counselor1, "언제가 편하신가요?", MessageType.TEXT);
        ChatMessage msg8 = createMessage(room2, user2, "내일 오후 2시 가능한가요?", MessageType.TEXT);

        // 이영희는 msg8까지 읽음 처리 (본인 메시지)
        updateLastReadMessage(room2, user2, msg8.getId());
        // 박상담은 msg7까지만 읽음 (msg8은 안읽음)
        updateLastReadMessage(room2, counselor1, msg7.getId());

        System.out.println("✅ 채팅방2에 메시지 3개 추가 (박상담 안읽음 1개)");

        // 5. 채팅방3에 메시지 추가 (김철수 - 최상담)
        ChatMessage msg9 = createMessage(room3, user1, "처음 상담 받아봅니다.", MessageType.TEXT);
        ChatMessage msg10 = createMessage(room3, counselor2, "환영합니다. 편하게 말씀해주세요.", MessageType.TEXT);

        // 둘 다 msg10까지 읽음 처리
        updateLastReadMessage(room3, user1, msg10.getId());
        updateLastReadMessage(room3, counselor2, msg10.getId());

        System.out.println("✅ 채팅방3에 메시지 2개 추가 (안읽음 없음)");

        System.out.println("========================================");
        System.out.println("테스트 데이터 초기화 완료!");
        System.out.println("========================================");
        System.out.println("\n📌 생성된 데이터:");
        System.out.println("- 회원: 4명 (사용자 2명, 상담사 2명)");
        System.out.println("- 채팅방: 3개");
        System.out.println("- 메시지: 10개");
        System.out.println("\n🔐 로그인 정보:");
        System.out.println("사용자1: user1@example.com / password123");
        System.out.println("사용자2: user2@example.com / password123");
        System.out.println("상담사1: counselor1@example.com / password123");
        System.out.println("상담사2: counselor2@example.com / password123");
        System.out.println("========================================\n");
    }

    /**
     * 회원 생성
     */
    private Member createUser(String email, String password, String name,
                              String phone, String address, Role role) {
        Member member = Member.builder()
                .email(email)
                .password(password)  // 실제로는 암호화 필요
                .name(name)
                .phone(phone)
                .address(address)
                .role(role)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build();
        return memberRepository.save(member);
    }

    /**
     * 채팅방 생성 및 멤버 추가
     */
    private ChatRoom createChatRoom(String roomName, Member member1, Member member2) {
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .roomType(RoomType.PRIVATE)
                .build();
        chatRoomRepository.save(chatRoom);

        // 채팅방 멤버 추가
        ChatRoomMember roomMember1 = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(member1)
                .lastReadMessageId(null)
                .build();
        chatRoomMemberRepository.save(roomMember1);

        ChatRoomMember roomMember2 = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(member2)
                .lastReadMessageId(null)
                .build();
        chatRoomMemberRepository.save(roomMember2);

        return chatRoom;
    }

    /**
     * 메시지 생성
     */
    private ChatMessage createMessage(ChatRoom chatRoom, Member sender,
                                      String content, MessageType type) {
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .type(type)
                .status(MessageStatus.SENT)
                .build();
        return chatMessageRepository.save(message);
    }

    /**
     * 마지막 읽은 메시지 업데이트
     */
    private void updateLastReadMessage(ChatRoom chatRoom, Member member, Long lastMessageId) {
        ChatRoomMember roomMember = chatRoomMemberRepository
                .findByChatRoomIdAndMemberId(chatRoom.getId(), member.getId())
                .orElseThrow();

        roomMember.setLastReadMessageId(lastMessageId);
        chatRoomMemberRepository.save(roomMember);
    }
}
