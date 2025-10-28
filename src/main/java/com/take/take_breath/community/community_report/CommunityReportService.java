package com.take.take_breath.community.community_report;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.community.community_post.CommunityPostRepository;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityReportService {

    private final CommunityReportRepository reportRepository;
    private final CommunityPostRepository postRepository;
    private final MemberRepository memberRepository;

    /**
     * 게시글 신고
     */
    @Transactional
    public CommunityReportResponse.CreateDTO createReport(Long postId, Long currentMemberId,
                                                          CommunityReportRequest.CreateDTO createDTO) {

        if (reportRepository.existsByReporterIdAndPostId(currentMemberId, postId)) {
            throw new Exception400("이미 신고한 게시글입니다.");
        }

        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다. ID: " + postId));

        if (post.isDeleted()) {
            throw new Exception400("삭제된 게시글은 신고할 수 없습니다.");
        }

        Member reporter = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new Exception404("회원을 찾을 수 없습니다."));

        CommunityReport report = CommunityReport.builder()
                .post(post)
                .reporter(reporter)
                .reason(createDTO.getReason())
                .status(CommunityReportStatus.PENDING)
                .build();

        CommunityReport savedReport = reportRepository.save(report);

        // 게시글 신고 횟수 증가
        post.increaseReportCount();

        log.info("[게시글 신고] reportId={}, postId={}, reporterId={}", savedReport.getId(), postId, currentMemberId);
        return CommunityReportResponse.CreateDTO.builder()
                .report(savedReport)
                .message("신고가 접수되었습니다.")
                .build();
    }

    /**
     * 내 신고 내역 목록 조회
     */
    public List<CommunityReportResponse.ListDTO> findAllMyReports(Long currentMemberId, Pageable pageable) {
        Page<CommunityReport> reports = reportRepository.findAllByReporterIdOrderByCreatedAtDesc(currentMemberId, pageable);
        return reports.stream()
                .map(report -> new CommunityReportResponse.ListDTO(report))
                .collect(Collectors.toList());
    }

    /**
     * 신고 내역 상세 조회
     */
    public CommunityReportResponse.DetailDTO detail(Long reportId, Long currentMemberId) {
        CommunityReport report = reportRepository.findByIdWithAdminComments(reportId)
                .orElseThrow(() -> new Exception404("신고 내역을 찾을 수 없습니다. ID: " + reportId));

        if (!report.isOwner(currentMemberId)) {
            throw new Exception403("본인이 신고한 내역만 조회할 수 있습니다.");
        }

        log.info("[신고 내역 상세 조회] reportId={}, memberId={}", reportId, currentMemberId);
        return new CommunityReportResponse.DetailDTO(report);
    }
}