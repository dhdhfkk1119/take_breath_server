package com.take.take_breath._core._config;

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
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import com.take.take_breath.terms.MemberTerms;
import com.take.take_breath.terms.MemberTermsRepository;
import com.take.take_breath.terms.Terms;
import com.take.take_breath.terms.TermsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
                .status(Status.ACTIVE)
                .build();

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

        Member reporterA = createReporter("reporterA@test.com", "신고자A");
        Member reporterB = createReporter("reporterB@test.com", "신고자B");
        Member reporterC = createReporter("reporterC@test.com", "신고자C");
        Member reporterD = createReporter("reporterD@test.com", "신고자D");
        memberRepository.saveAll(List.of(reporterA, reporterB, reporterC, reporterD));

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

        log.info("✅ 상담사 더미 + 신고 누적 테스트용 데이터 생성 완료");
        log.info("👤 일반 유저(user@test.com) → 게시글 3개 + 댓글 1개 작성 (정지 대상)");
        log.info("👨‍💼 관리자(admin@test.com)");
        log.info("🧑‍⚕️ 상담사: counselor1@test.com, counselor2@test.com");
        log.info("📢 신고자: reporterA~D@test.com (총 4명)");
        log.info("🚨 신고 승인 시 user 자동 정지 로직 테스트 가능");
        log.info("============================================");
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
}
