package com.take.take_breath._core._config;

import com.take.take_breath.chat.chat_room_member.ChatRoomMember;
import com.take.take_breath.chat.chat_room_member.ChatRoomMemberRepository;
import com.take.take_breath.chat.chat_message.ChatMessage;
import com.take.take_breath.chat.chat_message.ChatMessageRepository;
import com.take.take_breath.chat.chat_message.MessageStatus;
import com.take.take_breath.chat.chat_message.MessageType;
import com.take.take_breath.chat.chat_room.ChatRoom;
import com.take.take_breath.chat.chat_room.ChatRoomRepository;
import com.take.take_breath.chat.chat_room.RoomType;
import com.take.take_breath.counselor.entity.Counselor;
import com.take.take_breath.counselor.repository.CounselorRepository;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import com.take.take_breath.members.entity.Member;
import com.take.take_breath.members.repository.MemberRepository;
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
    private final CounselorRepository counselorRepository;

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

        // 1. 일반 회원 생성 (USER)
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

        System.out.println("✅ 일반 회원 2명 생성 완료");

        // 2. 상담사 회원 생성 (COUNSELOR + Counselor 프로필)
        Member counselorMember1 = createCounselor(
                "counselor1@example.com",
                "password123",
                "박상담",
                "010-5555-6666",
                "서울시 강남구",
                "심리상담사 1급",              // license
                "불안장애, 우울증, 스트레스",    // specialty
                "10년 경력의 전문 상담사입니다. 편안한 상담을 제공합니다.",  // introduction
                "여성",                        // gender
                null,                          // profileImage (나중에 추가)
                "#인지행동치료 #CBT #우울증전문",  // hashtags
                50000                          // price
        );

        Member counselorMember2 = createCounselor(
                "counselor2@example.com",
                "password123",
                "최상담",
                "010-7777-8888",
                "서울시 송파구",
                "상담심리사 2급",
                "가족상담, 부부상담, 청소년상담",
                "따뜻한 마음으로 함께 고민을 나누겠습니다.",
                "남성",
                null,
                "#가족상담 #청소년 #부부상담",
                60000
        );

        System.out.println("✅ 상담사 2명 생성 완료 (Counselor 프로필 포함)");

        // 3. 채팅방 생성 및 멤버 추가
        ChatRoom room1 = createChatRoom("김철수 - 박상담", user1, counselorMember1);
        ChatRoom room2 = createChatRoom("이영희 - 박상담", user2, counselorMember1);
        ChatRoom room3 = createChatRoom("김철수 - 최상담", user1, counselorMember2);

        System.out.println("✅ 채팅방 3개 생성 완료");

        // 4. 채팅방1에 메시지 추가 (김철수 - 박상담)
        ChatMessage msg1 = createMessage(room1, user1, "안녕하세요. 상담 신청합니다.", MessageType.TEXT);
        ChatMessage msg2 = createMessage(room1, counselorMember1, "네, 안녕하세요. 무엇을 도와드릴까요?", MessageType.TEXT);
        ChatMessage msg3 = createMessage(room1, user1, "요즘 스트레스가 심해서 상담받고 싶습니다.", MessageType.TEXT);
        ChatMessage msg4 = createMessage(room1, counselorMember1, "자세히 말씀해주시겠어요?", MessageType.TEXT);
        ChatMessage msg5 = createMessage(room1, user1, "직장에서 업무 압박이 심합니다.", MessageType.TEXT);

        // 김철수는 msg3까지 읽음 처리 (msg4, msg5 안읽음)
        updateLastReadMessage(room1, user1, msg3.getId());
        // 박상담은 msg5까지 모두 읽음 처리
        updateLastReadMessage(room1, counselorMember1, msg5.getId());

        System.out.println("✅ 채팅방1에 메시지 5개 추가 (김철수 안읽음 2개)");

        // 5. 채팅방2에 메시지 추가 (이영희 - 박상담)
        ChatMessage msg6 = createMessage(room2, user2, "상담 예약하고 싶습니다.", MessageType.TEXT);
        ChatMessage msg7 = createMessage(room2, counselorMember1, "언제가 편하신가요?", MessageType.TEXT);
        ChatMessage msg8 = createMessage(room2, user2, "내일 오후 2시 가능한가요?", MessageType.TEXT);

        // 이영희는 msg8까지 읽음 처리 (본인 메시지)
        updateLastReadMessage(room2, user2, msg8.getId());
        // 박상담은 msg7까지만 읽음 (msg8 안읽음)
        updateLastReadMessage(room2, counselorMember1, msg7.getId());

        System.out.println("✅ 채팅방2에 메시지 3개 추가 (박상담 안읽음 1개)");

        // 6. 채팅방3에 메시지 추가 (김철수 - 최상담)
        ChatMessage msg9 = createMessage(room3, user1, "처음 상담 받아봅니다.", MessageType.TEXT);
        ChatMessage msg10 = createMessage(room3, counselorMember2, "환영합니다. 편하게 말씀해주세요.", MessageType.TEXT);

        // 둘 다 msg10까지 읽음 처리
        updateLastReadMessage(room3, user1, msg10.getId());
        updateLastReadMessage(room3, counselorMember2, msg10.getId());

    }

    /**
     * 일반 회원 생성 (USER)
     */
    private Member createUser(String email, String password, String name,
                              String phone, String address, Role role) {
        Member member = Member.builder()
                .email(email)
                .password(password)  // ⚠️ 실제로는 암호화 필요 (BCryptPasswordEncoder)
                .name(name)
                .phone(phone)
                .address(address)
                .role(role)
                .status(Status.ACTIVE)
                .emailVerified(true)
                // 약관 동의 (필수)
                .termsService(true)
                .termsPrivacy(true)
                .termsThirdParty(true)
                .termsMarketing(false)  // 마케팅 수신 동의는 선택
                .build();
        return memberRepository.save(member);
    }

    /**
     * 상담사 회원 생성 (COUNSELOR + Counselor 프로필)
     */
    private Member createCounselor(String email, String password, String name,
                                   String phone, String address,
                                   String license, String specialty, String introduction,
                                   String gender, String profileImage, String hashtags,
                                   int price) {
        // 1. Member 생성 (COUNSELOR 역할)
        Member member = Member.builder()
                .email(email)
                .password(password)  // ⚠️ 실제로는 암호화 필요
                .name(name)
                .phone(phone)
                .address(address)
                .role(Role.COUNSELOR)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .termsService(true)
                .termsPrivacy(true)
                .termsThirdParty(true)
                .termsMarketing(false)
                .build();
        memberRepository.save(member);

        // 2. Counselor 프로필 생성
        Counselor counselor = Counselor.builder()
                .member(member)
                .license(license)
                .specialty(specialty)
                .introduction(introduction)
                .gender(gender)
                .profileImage(profileImage)
                .hashtags(hashtags)
                .price(price)
                .build();
        counselorRepository.save(counselor);

        // 3. Member에 Counselor 연결 (양방향 관계 설정)
        member.setCounselor(counselor);

        return member;
    }

    /**
     * 채팅방 생성 및 멤버 추가
     */
    private ChatRoom createChatRoom(String roomName, Member member1, Member member2) {
        // 1. 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .roomType(RoomType.PRIVATE)  // 1:1 채팅
                .build();
        chatRoomRepository.save(chatRoom);

        // 2. 채팅방 멤버 추가 (member1)
        ChatRoomMember roomMember1 = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(member1)
                .lastReadMessageId(null)  // 초기값 null
                .build();
        chatRoomMemberRepository.save(roomMember1);

        // 3. 채팅방 멤버 추가 (member2)
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
                .orElseThrow(() -> new IllegalStateException(
                        "채팅방 멤버를 찾을 수 없습니다. chatRoomId=" + chatRoom.getId() +
                                ", memberId=" + member.getId()
                ));

        roomMember.setLastReadMessageId(lastMessageId);
        chatRoomMemberRepository.save(roomMember);
    }
}
