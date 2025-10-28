package com.take.take_breath.community.community_report;

import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.community.community_post.CommunityPostRepository;
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

    /**
     * 게시글 신고
     */
    @Transactional
    public CommunityReportResponse.CreateDTO createReport(Long postId, Long currentUserId,
                                                          CommunityReportRequest.CreateDTO createDTO) {
        // Early return pattern: 중복 신고 체크
        if (reportRepository.existsByReporterIdAndPostId(currentUserId, postId)) {
            throw new IllegalArgumentException("이미 신고한 게시글입니다.");
        }

        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        if (post.isDeleted()) {
            throw new IllegalArgumentException("삭제된 게시글은 신고할 수 없습니다.");
        }

        CommunityReport report = CommunityReport.builder()
                .post(post)
                .reporterId(currentUserId)
                .reason(createDTO.getReason())
                .status(CommunityReportStatus.PENDING)
                .build();

        CommunityReport savedReport = reportRepository.save(report);

        // 게시글 신고 횟수 증가
        post.increaseReportCount();

        log.info("[게시글 신고] reportId={}, postId={}, reporterId={}", savedReport.getId(), postId, currentUserId);
        return CommunityReportResponse.CreateDTO.builder()
                .report(savedReport)
                .message("신고가 접수되었습니다.")
                .build();
    }

    /**
     * 내 신고 내역 목록 조회
     */
    public List<CommunityReportResponse.ListDTO> findAllMyReports(Long currentUserId, Pageable pageable) {
        Page<CommunityReport> reports = reportRepository.findAllByReporterIdOrderByCreatedAtDesc(currentUserId, pageable);
        return reports.stream()
                .map(report -> new CommunityReportResponse.ListDTO(report))
                .collect(Collectors.toList());
    }

    /**
     * 신고 내역 상세 조회
     */
    public CommunityReportResponse.DetailDTO detail(Long reportId, Long currentUserId) {
        CommunityReport report = reportRepository.findByIdWithAdminComments(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다. ID: " + reportId));

        if (!report.isOwner(currentUserId)) {
            throw new IllegalArgumentException("본인이 신고한 내역만 조회할 수 있습니다.");
        }

        log.info("[신고 내역 상세 조회] reportId={}, userId={}", reportId, currentUserId);
        return new CommunityReportResponse.DetailDTO(report);
    }
}