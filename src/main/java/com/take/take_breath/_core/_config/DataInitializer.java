package com.take.take_breath._core._config;

import com.take.take_breath.chat.chat_message.ChatMessage;
import com.take.take_breath.chat.chat_message.ChatMessageRepository;
import com.take.take_breath.chat.chat_message.MessageStatus;
import com.take.take_breath.chat.chat_message.MessageType;
import com.take.take_breath.chat.chat_room.ChatRoom;
import com.take.take_breath.chat.chat_room.ChatRoomRepository;
import com.take.take_breath.chat.chat_room.RoomType;
import com.take.take_breath.chat.chat_room_member.ChatRoomMember;
import com.take.take_breath.chat.chat_room_member.ChatRoomMemberRepository;
import com.take.take_breath.community.comment_report.CommentReport;
import com.take.take_breath.community.comment_report.CommentReportRepository;
import com.take.take_breath.community.community_category.CommunityCategory;
import com.take.take_breath.community.community_category.CommunityCategoryRepository;
import com.take.take_breath.community.community_comment.CommunityComment;
import com.take.take_breath.community.community_comment.CommunityCommentRepository;
import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.community.community_post.CommunityPostRepository;
import com.take.take_breath.community.community_report.CommunityReport;
import com.take.take_breath.community.community_report.CommunityReportRepository;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.counselor.CounselorLicense;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import com.take.take_breath.payment.Payment;
import com.take.take_breath.payment.PaymentRepository;
import com.take.take_breath.payment.PaymentStatus;
import com.take.take_breath.record.FileType;
import com.take.take_breath.record.RecordFile;
import com.take.take_breath.record.RecordFileRepository;
import com.take.take_breath.record.RecordRepository;
import com.take.take_breath.terms.MemberTerms;
import com.take.take_breath.terms.MemberTermsRepository;
import com.take.take_breath.terms.Terms;
import com.take.take_breath.terms.TermsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final CounselorRepository counselorRepository;
    private final PasswordEncoder passwordEncoder;
    private final TermsRepository termsRepository;
    private final MemberTermsRepository memberTermsRepository;
    private final CommunityCategoryRepository communityCategoryRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityReportRepository communityReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final RecordRepository recordRepository;
    private final RecordFileRepository recordFileRepository;
    private final PaymentRepository paymentRepository;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void run(String... args) throws Exception {
        Terms terms1 = Terms.builder()
                .title("서비스 이용약관")
                .content("서비스 이용약관 내용")
                .required(true)
                .build();

        Terms terms2 = Terms.builder()
                .title("개인정보 처리방침")
                .content("개인정보 처리방침 내용")
                .required(true)
                .build();

        termsRepository.saveAll(List.of(terms1, terms2));

        Member user = Member.builder()
                .email("user@test.com")
                .password(passwordEncoder.encode("1234"))
                .name("테스트유저")
                .phone("01012345678")
                .address("서울시 강남구")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .point(10000L)
                .build();

        memberRepository.save(user);
        saveMemberTerms(user, List.of(terms1, terms2));

        Member admin = Member.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("1234"))
                .name("관리자")
                .phone("01087654321")
                .address("서울시 서초구")
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build();
        memberRepository.save(admin);
        saveMemberTerms(admin, List.of(terms1, terms2));

        // ✅ 기록실 샘플 데이터 생성
        createSampleRecords(user);

        // 상담사 Member 생성
        Member counselorMember1 = Member.builder()
                .email("counselor1@test.com")
                .password(passwordEncoder.encode("1234"))
                .name("상담사 김하늘")
                .phone("01099998888")
                .address("서울시 송파구")
                .role(Role.COUNSELOR)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build();

        Member counselorMember2 = Member.builder()
                .email("counselor2@test.com")
                .password(passwordEncoder.encode("1234"))
                .name("상담사 이바다")
                .phone("01099997777")
                .address("서울시 종로구")
                .role(Role.COUNSELOR)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build();

        memberRepository.saveAll(List.of(counselorMember1, counselorMember2));
        saveMemberTerms(counselorMember1, List.of(terms1, terms2));
        saveMemberTerms(counselorMember2, List.of(terms1, terms2));

        Counselor counselor1 = Counselor.builder()
                .member(counselorMember1)
                .introduction("10년차 심리상담사입니다.")
                .gender("여성")
                .profileImage("default_profile1.png")
                .specialty("직장 내 스트레스, 불안")
                .price(50000)
                .hashtags("#스트레스 #불안 #직장인")
                .point(0)
                .status(Status.PENDING)
                .build();

        counselor1.setLicenses(List.of(
                CounselorLicense.builder()
                        .licenseName("임상심리사 2급")
                        .licenseNumber("PSY-2024-001")
                        .licenseRegiNumber("REG-001-2024")
                        .licenseImage("license_kimhaneul_1.jpg")
                        .counselor(counselor1)
                        .build(),
                CounselorLicense.builder()
                        .licenseName("심리상담사 1급")
                        .licenseNumber("CS-2024-045")
                        .licenseRegiNumber("REG-045-2024")
                        .licenseImage("license_kimhaneul_2.jpg")
                        .counselor(counselor1)
                        .build()
        ));

        Counselor counselor2 = Counselor.builder()
                .member(counselorMember2)
                .introduction("대인관계, 번아웃, 우울 관련 상담을 진행합니다.")
                .gender("남성")
                .profileImage("default_profile2.png")
                .specialty("대인관계, 번아웃, 우울")
                .price(60000)
                .hashtags("#대인관계 #번아웃 #우울")
                .point(0)
                .status(Status.ACTIVE)
                .build();

        counselorRepository.saveAll(List.of(counselor1, counselor2));

        // 신고 테스트 데이터 이하 동일...
        // (생략하지 않고 계속 유지)
        Member reporterA = createReporter("reporterA@test.com", "신고자A");
        Member reporterB = createReporter("reporterB@test.com", "신고자B");
        Member reporterC = createReporter("reporterC@test.com", "신고자C");
        Member reporterD = createReporter("reporterD@test.com", "신고자D");
        memberRepository.saveAll(List.of(reporterA, reporterB, reporterC, reporterD));

        // 커뮤니티 카테고리/게시글
        CommunityCategory category = communityCategoryRepository.save(
                CommunityCategory.builder().name("신고 테스트 게시판").build()
        );

        CommunityPost post1 = createPost("신고 테스트용 게시글 A", category, user);
        CommunityPost post2 = createPost("신고 테스트용 게시글 B", category, user);
        CommunityPost post3 = createPost("신고 테스트용 게시글 C", category, user);
        communityPostRepository.saveAll(List.of(post1, post2, post3));

        CommunityComment comment1 = communityCommentRepository.save(
                CommunityComment.builder()
                        .content("신고 테스트용 댓글입니다.")
                        .post(post1)
                        .member(user)
                        .reportCount(0)
                        .build()
        );

        communityReportRepository.saveAll(List.of(
                createPostReport(post1, reporterA, "스팸 게시글 의심"),
                createPostReport(post2, reporterB, "부적절한 표현 포함"),
                createPostReport(post3, reporterC, "욕설 포함")
        ));

        commentReportRepository.save(CommentReport.builder()
                .reporter(reporterD)
                .comment(comment1)
                .reason("악의적인 댓글입니다.")
                .status(CommunityReportStatus.PENDING)
                .build());

        // ===
        ChatRoom counselingRoom = ChatRoom.builder()
                .name("test채팅방")
                .roomType(RoomType.COUNSELING)
                .build();
        chatRoomRepository.save(counselingRoom);

        // 2. 채팅방에 멤버 추가 (testUser)
        ChatRoomMember userMember = ChatRoomMember.builder()
                .chatRoom(counselingRoom)
                .member(user)
                .lastReadMessageId(null)
                .build();
        chatRoomMemberRepository.save(userMember);

        // 3. 채팅방에 멤버 추가 (counselor1)
        ChatRoomMember counselorMember = ChatRoomMember.builder()
                .chatRoom(counselingRoom)
                .member(counselorMember1)
                .lastReadMessageId(null)
                .build();
        chatRoomMemberRepository.save(counselorMember);

        ChatMessage message1 = ChatMessage.builder()
                .chatRoom(counselingRoom)
                .sender(counselorMember1)
                .content("안녕하세요! 상담사 김하늘입니다. 편하게 말씀해주세요 😊")
                .type(MessageType.TEXT)
                .status(MessageStatus.READ)
                .build();
        chatMessageRepository.save(message1);

        ChatMessage message2 = ChatMessage.builder()
                .chatRoom(counselingRoom)
                .sender(user)
                .content("안녕하세요. 요즘 직장에서 스트레스가 너무 심해서 상담 받고 싶어요.")
                .type(MessageType.TEXT)
                .status(MessageStatus.READ)
                .build();
        chatMessageRepository.save(message2);

        ChatMessage message3 = ChatMessage.builder()
                .chatRoom(counselingRoom)
                .sender(counselorMember1)
                .content("직장 스트레스로 힘드시군요. 구체적으로 어떤 상황이 가장 힘드신가요?")
                .type(MessageType.TEXT)
                .status(MessageStatus.READ)
                .build();
        chatMessageRepository.save(message3);

        ChatMessage message4 = ChatMessage.builder()
                .chatRoom(counselingRoom)
                .sender(user)
                .content("업무량이 너무 많고, 상사와의 관계도 좋지 않아요. 매일 퇴근하면 기진맥진합니다.")
                .type(MessageType.TEXT)
                .status(MessageStatus.READ)
                .build();
        chatMessageRepository.save(message4);

        ChatMessage message5 = ChatMessage.builder()
                .chatRoom(counselingRoom)
                .sender(counselorMember1)
                .content("많이 힘드셨겠어요. 업무 부담과 대인관계 문제가 함께 겹치면 더욱 지치실 수 있습니다.")
                .type(MessageType.TEXT)
                .status(MessageStatus.READ)
                .build();
        chatMessageRepository.save(message5);

        ChatMessage message6 = ChatMessage.builder()
                .chatRoom(counselingRoom)
                .sender(counselorMember1)
                .content("우선 하루 일과 중 본인만의 휴식 시간을 확보하는 것이 중요합니다. 점심시간이나 퇴근 후 짧은 산책도 도움이 될 수 있어요.")
                .type(MessageType.TEXT)
                .status(MessageStatus.SENT)
                .build();
        chatMessageRepository.save(message6);

        ChatMessage systemMessage = ChatMessage.builder()
                .chatRoom(counselingRoom)
                .sender(counselorMember1)
                .content("상담 시간이 30분 남았습니다.")
                .type(MessageType.SYSTEM)
                .status(MessageStatus.SENT)
                .build();
        chatMessageRepository.save(systemMessage);

        userMember.updateLastRead(message5.getId());
        chatRoomMemberRepository.save(userMember);

        counselorMember.updateLastRead(systemMessage.getId());
        chatRoomMemberRepository.save(counselorMember);


        log.info("✅ 상담사 더미 + 신고 누적 테스트용 데이터 생성 완료");
        log.info("일반 유저(user@test.com) → 게시글 3개 + 댓글 1개 작성 (정지 대상)");
        log.info("관리자(admin@test.com)");
        log.info("상담사: counselor1@test.com, counselor2@test.com");
        log.info("신고자: reporterA~D@test.com (총 4명)");
        log.info("신고 승인 시 user 자동 정지 로직 테스트 가능");
        log.info("============================================");

        log.info("✅ 데이터 생성 완료");
        // ✅ 결제 더미 데이터 추가
        paymentRepository.saveAll(List.of(
                // 5월
                Payment.builder()
                        .member(user)
                        .impUid("imp_00501")
                        .merchantUid("order_00501")
                        .pointAmount(8000L)
                        .feeAmount(800L)
                        .feeRate(0.1)
                        .amount(8800L)
                        .status(PaymentStatus.PAID)
                        .payMethod("card")
                        .orderName("포인트 충전 0.8만P")
                        .buyerName("테스트회원")
                        .buyerEmail("user1@test.com")
                        .buyerTel("010-1111-1111")
                        .createdAt(Timestamp.valueOf("2025-05-12 10:00:00"))
                        .paidAt(Timestamp.valueOf("2025-05-12 10:01:00"))
                        .build(),

                // 6월
                Payment.builder()
                        .member(user)
                        .impUid("imp_00601")
                        .merchantUid("order_00601")
                        .pointAmount(12000L)
                        .feeAmount(1200L)
                        .feeRate(0.1)
                        .amount(13200L)
                        .status(PaymentStatus.PAID)
                        .payMethod("card")
                        .orderName("포인트 충전 1.2만P")
                        .buyerName("테스트회원")
                        .buyerEmail("user1@test.com")
                        .buyerTel("010-1111-1111")
                        .createdAt(Timestamp.valueOf("2025-06-08 14:20:00"))
                        .paidAt(Timestamp.valueOf("2025-06-08 14:21:00"))
                        .build(),

                // 7월
                Payment.builder()
                        .member(user)
                        .impUid("imp_00701")
                        .merchantUid("order_00701")
                        .pointAmount(15000L)
                        .feeAmount(1500L)
                        .feeRate(0.1)
                        .amount(16500L)
                        .status(PaymentStatus.PAID)
                        .payMethod("kakaopay")
                        .orderName("포인트 충전 1.5만P")
                        .buyerName("테스트회원")
                        .buyerEmail("user1@test.com")
                        .buyerTel("010-1111-1111")
                        .createdAt(Timestamp.valueOf("2025-07-19 16:00:00"))
                        .paidAt(Timestamp.valueOf("2025-07-19 16:01:00"))
                        .build(),

                // 8월
                Payment.builder()
                        .member(user)
                        .impUid("imp_00801")
                        .merchantUid("order_00801")
                        .pointAmount(20000L)
                        .feeAmount(2000L)
                        .feeRate(0.1)
                        .amount(22000L)
                        .status(PaymentStatus.PAID)
                        .payMethod("card")
                        .orderName("포인트 충전 2만P")
                        .buyerName("테스트회원")
                        .buyerEmail("user1@test.com")
                        .buyerTel("010-1111-1111")
                        .createdAt(Timestamp.valueOf("2025-08-04 11:00:00"))
                        .paidAt(Timestamp.valueOf("2025-08-04 11:01:00"))
                        .build(),

                // 9월
                Payment.builder()
                        .member(user)
                        .impUid("imp_00901")
                        .merchantUid("order_00901")
                        .pointAmount(25000L)
                        .feeAmount(2500L)
                        .feeRate(0.1)
                        .amount(27500L)
                        .status(PaymentStatus.PAID)
                        .payMethod("card")
                        .orderName("포인트 충전 2.5만P")
                        .buyerName("테스트회원")
                        .buyerEmail("user1@test.com")
                        .buyerTel("010-1111-1111")
                        .createdAt(Timestamp.valueOf("2025-09-10 09:30:00"))
                        .paidAt(Timestamp.valueOf("2025-09-10 09:31:00"))
                        .build(),

                // 10월
                Payment.builder()
                        .member(user)
                        .impUid("imp_01001")
                        .merchantUid("order_01001")
                        .pointAmount(30000L)
                        .feeAmount(3000L)
                        .feeRate(0.1)
                        .amount(33000L)
                        .status(PaymentStatus.PAID)
                        .payMethod("card")
                        .orderName("포인트 충전 3만P")
                        .buyerName("테스트회원")
                        .buyerEmail("user1@test.com")
                        .buyerTel("010-1111-1111")
                        .createdAt(Timestamp.valueOf("2025-10-17 18:00:00"))
                        .paidAt(Timestamp.valueOf("2025-10-17 18:01:00"))
                        .build(),

                // 11월
                Payment.builder()
                        .member(user)
                        .impUid("imp_01101")
                        .merchantUid("order_01101")
                        .pointAmount(35000L)
                        .feeAmount(3500L)
                        .feeRate(0.1)
                        .amount(38500L)
                        .status(PaymentStatus.PAID)
                        .payMethod("kakaopay")
                        .orderName("포인트 충전 3.5만P")
                        .buyerName("테스트회원")
                        .buyerEmail("user1@test.com")
                        .buyerTel("010-1111-1111")
                        .createdAt(Timestamp.valueOf("2025-11-10 13:30:00"))
                        .paidAt(Timestamp.valueOf("2025-11-10 13:31:00"))
                        .build()
        ));

        log.info("결제 더미 데이터 생성 완료 (10월~11월)");
        log.info("상담사/신고/회원 테스트 데이터 생성 완료");
    }

    // 메서드
    private void saveMemberTerms(Member member, List<Terms> termsList) {
        for (Terms t : termsList) {
            memberTermsRepository.save(MemberTerms.builder()
                    .member(member)
                    .terms(t)
                    .agreed(true)
                    .agreedAt(LocalDateTime.now())
                    .build());
        }
    }

    private Member createReporter(String email, String name) {
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode("1234"))
                .name(name)
                .phone("010" + (int) (Math.random() * 99999999))
                .address("서울시 테스트구")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build();
    }

    private CommunityPost createPost(String title, CommunityCategory category, Member member) {
        return CommunityPost.builder()
                .title(title)
                .content("이 게시글은 신고 누적 테스트용입니다.")
                .category(category)
                .member(member)
                .likeCount(0)
                .viewCount(0)
                .reportCount(0)
                .build();
    }

    private CommunityReport createPostReport(CommunityPost post, Member reporter, String reason) {
        return CommunityReport.builder()
                .reporter(reporter)
                .post(post)
                .reason(reason)
                .status(CommunityReportStatus.PENDING)
                .build();
    }


    private void createSampleRecords(Member user) {
        String[] titles = {
                "첫 번째 테스트 기록", "오늘의 감정", "스트레스 관리 팁", "일상 속 작은 행복", "명상 경험기",
                "마음이 편했던 날", "자기 성찰 기록", "감정 정리하기", "긍정적인 생각", "하루를 돌아보며"
        };

        String[] contents = {
                "이것은 서버 시작 시 자동으로 생성된 테스트 기록입니다.",
                "오늘 하루 기분을 기록해보세요.",
                "스트레스를 줄이는 방법에 대해 생각해봅시다.",
                "작은 것에 감사하는 하루.",
                "명상으로 마음을 다스린 날.",
                "기분이 좋았던 순간을 남깁니다.",
                "자기 자신을 들여다보는 시간.",
                "감정을 글로 풀어내기.",
                "긍정적인 방향으로 생각하기.",
                "하루를 되돌아보는 기록."
        };

        for (int i = 0; i < 10; i++) {
            com.take.take_breath.record.Record record = recordRepository.save(
                    com.take.take_breath.record.Record.builder()
                            .member(user)
                            .title(titles[i])
                            .content(contents[i])
                            .recordDate(new java.sql.Timestamp(System.currentTimeMillis() - (i * 86400000)))
                            .build()
            );

            RecordFile image = RecordFile.builder()
                    .record(record)
                    .fileType(FileType.IMAGE)
                    .fileName("sample_001.png")
                    .originalFileName("sample_001.png")
                    .filePath("/uploads/records/images/sample_001.png")
                    .fileSize(204800L)
                    .contentType("image/png")
                    .build();

            recordFileRepository.save(image);

            record.addRecordFile(image);
            recordRepository.save(record);
        }
    }
}
