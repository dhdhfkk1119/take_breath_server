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

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
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
        // 약관 생성
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

        termsRepository.save(terms1);
        termsRepository.save(terms2);

        // 일반 사용자
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

        // 약관 동의
        memberTermsRepository.save(MemberTerms.builder()
                .member(user)
                .terms(terms1)
                .agreed(true)
                .agreedAt(LocalDateTime.now())
                .build());

        memberTermsRepository.save(MemberTerms.builder()
                .member(user)
                .terms(terms2)
                .agreed(true)
                .agreedAt(LocalDateTime.now())
                .build());

        // 관리자
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

        memberTermsRepository.save(MemberTerms.builder()
                .member(admin)
                .terms(terms1)
                .agreed(true)
                .agreedAt(LocalDateTime.now())
                .build());

        memberTermsRepository.save(MemberTerms.builder()
                .member(admin)
                .terms(terms2)
                .agreed(true)
                .agreedAt(LocalDateTime.now())
                .build());

        // 카테고리 생성
        CommunityCategory category1 = communityCategoryRepository.save(CommunityCategory.builder()
                .name("자유게시판")
                .build());

        CommunityCategory category2 = communityCategoryRepository.save(CommunityCategory.builder()
                .name("정보공유")
                .build());

        CommunityCategory category3 = communityCategoryRepository.save(CommunityCategory.builder()
                .name("질문답변")
                .build());

// ===== 게시글 더미 데이터 (모두 user가 작성한 글) =====
        CommunityPost post1 = communityPostRepository.save(CommunityPost.builder()
                .title("오늘 점심 뭐 드셨나요?")
                .content("직장인 점심 추천 좀 해주세요!")
                .likeCount(3)
                .viewCount(15)
                .reportCount(0)
                .category(category1)
                .member(user)
                .build());

        CommunityPost post2 = communityPostRepository.save(CommunityPost.builder()
                .title("회사 근처 맛집 공유합니다")
                .content("강남역 근처 맛집 리스트 공유드려요.")
                .likeCount(5)
                .viewCount(40)
                .reportCount(0)
                .category(category2)
                .member(user) // 👈 작성자 동일 (user)
                .build());

        CommunityPost post3 = communityPostRepository.save(CommunityPost.builder()
                .title("업무 스트레스 줄이는 방법 있을까요?")
                .content("요즘 너무 피곤해서 스트레스 관리가 힘들어요.")
                .likeCount(2)
                .viewCount(10)
                .reportCount(0)
                .category(category3)
                .member(user) // 👈 작성자 동일 (user)
                .build());

// ===== 댓글 더미 (그냥 참고용) =====
        CommunityComment comment1 = communityCommentRepository.save(CommunityComment.builder()
                .content("좋은 글이네요! 공감합니다.")
                .post(post1)
                .member(admin)
                .reportCount(0)
                .build());

        CommunityComment comment2 = communityCommentRepository.save(CommunityComment.builder()
                .content("저도 비슷한 고민을 하고 있었어요.")
                .post(post2)
                .member(admin)
                .reportCount(0)
                .build());

// ===== 게시글 신고 더미 데이터 (모두 user의 게시글을 admin이 신고) =====
        communityReportRepository.save(CommunityReport.builder()
                .reporter(admin)
                .post(post1)
                .reason("광고성 게시글 같습니다.")
                .status(CommunityReportStatus.PENDING)
                .build());

        communityReportRepository.save(CommunityReport.builder()
                .reporter(admin)
                .post(post2)
                .reason("부적절한 내용이 포함되어 있습니다.")
                .status(CommunityReportStatus.PENDING)
                .build());

        communityReportRepository.save(CommunityReport.builder()
                .reporter(admin)
                .post(post3)
                .reason("욕설이 포함되어 있습니다.")
                .status(CommunityReportStatus.PENDING)
                .build());

// ===== 댓글 신고 더미 데이터 (테스트용) =====
        commentReportRepository.save(CommentReport.builder()
                .reporter(admin)
                .comment(comment1)
                .reason("부적절한 표현이 있습니다.")
                .status(CommunityReportStatus.PENDING)
                .build());

        commentReportRepository.save(CommentReport.builder()
                .reporter(admin)
                .comment(comment2)
                .reason("광고성 댓글 같습니다.")
                .status(CommunityReportStatus.PENDING)
                .build());
        log.info("===== 더미 데이터 생성 완료 =====");
        log.info("일반 유저: user@test.com / 1234");
        log.info("관리자: admin@test.com / 1234");
        log.info("게시글: 3개, 댓글: 3개");
        log.info("게시글 신고: 3개, 댓글 신고: 2개");
        log.info("================================");
    }
}