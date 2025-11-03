package com.take.take_breath._core._config;

import com.take.take_breath.community.community_category.CommunityCategory;
import com.take.take_breath.community.community_category.CommunityCategoryRepository;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import com.take.take_breath.record.*;
import com.take.take_breath.record.Record;
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
    private final PasswordEncoder passwordEncoder;
    private final TermsRepository termsRepository;
    private final MemberTermsRepository memberTermsRepository;
    private final CommunityCategoryRepository communityCategoryRepository;
    private final RecordRepository recordRepository;
    private final RecordFileRepository recordFileRepository;

    // 이니셜라이즈 회원가입 멤버 추가
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
        communityCategoryRepository.save(CommunityCategory.builder()
                .name("자유게시판")
                .build());

        communityCategoryRepository.save(CommunityCategory.builder()
                .name("정보공유")
                .build());

        communityCategoryRepository.save(CommunityCategory.builder()
                .name("질문답변")
                .build());

        // 기록에 대한 더미 데이터 생성
        Record record = Record.builder()
                .member(user)
                .title("첫 번째 테스트 기록")
                .content("이것은 서버 시작 시 자동으로 생성된 테스트 기록입니다.")
                .build();
        Record savedRecord = recordRepository.save(record);

        // RecordFile (첨부파일) 생성 예시
        RecordFile imageFile = RecordFile.builder()
                .record(savedRecord)
                .fileType(FileType.IMAGE)
                .fileName("sample_001.png")
                .originalFileName("sample_001.png")
                .filePath("/uploads/record/images/sample_001.png")
                .fileSize(204800L)
                .contentType("image/png")
                .build();
        recordFileRepository.save(imageFile);

        savedRecord.addRecordFile(imageFile);
        recordRepository.save(savedRecord);

        log.info("===== 더미 데이터 생성 완료 =====");
        log.info("일반 유저: user@test.com / 1234");
        log.info("관리자: admin@test.com / 1234");
        log.info("================================");
    }
    
    
}