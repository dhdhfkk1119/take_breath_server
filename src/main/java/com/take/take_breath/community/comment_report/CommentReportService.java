package com.take.take_breath.community.comment_report;

import com.take.take_breath.community.community_comment.CommunityComment;
import com.take.take_breath.community.community_comment.CommunityCommentRepository;
import com.take.take_breath.community.community_report.CommunityReportStatus;
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
public class CommentReportService {

    private final CommentReportRepository reportRepository;
    private final CommunityCommentRepository commentRepository;

    /**
     * 댓글 신고
     */
    @Transactional
    public CommentReportResponse.CreateDTO createReport(Long commentId, Long currentUserId,
                                                        CommentReportRequest.CreateDTO createDTO) {
        // Early return pattern: 중복 신고 체크
        if (reportRepository.existsByReporterIdAndCommentId(currentUserId, commentId)) {
            throw new IllegalArgumentException("이미 신고한 댓글입니다.");
        }

        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (comment.isDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글은 신고할 수 없습니다.");
        }

        CommentReport report = CommentReport.builder()
                .comment(comment)
                .reporterId(currentUserId)
                .reason(createDTO.getReason())
                .status(CommunityReportStatus.PENDING)
                .build();

        CommentReport savedReport = reportRepository.save(report);

        // 댓글 신고 횟수 증가
        comment.increaseReportCount();

        log.info("[댓글 신고] reportId={}, commentId={}, reporterId={}", savedReport.getId(), commentId, currentUserId);
        return CommentReportResponse.CreateDTO.builder()
                .report(savedReport)
                .message("신고가 접수되었습니다.")
                .build();
    }

    /**
     * 내 신고 내역 목록 조회
     */
    public List<CommentReportResponse.ListDTO> findAllMyReports(Long currentUserId, Pageable pageable) {
        Page<CommentReport> reports = reportRepository.findAllByReporterIdOrderByCreatedAtDesc(currentUserId, pageable);
        return reports.stream()
                .map(report -> new CommentReportResponse.ListDTO(report))
                .collect(Collectors.toList());
    }

    /**
     * 신고 내역 상세 조회
     */
    public CommentReportResponse.DetailDTO detail(Long reportId, Long currentUserId) {
        CommentReport report = reportRepository.findByIdWithAdminComments(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다. ID: " + reportId));

        if (!report.isOwner(currentUserId)) {
            throw new IllegalArgumentException("본인이 신고한 내역만 조회할 수 있습니다.");
        }

        log.info("[신고 내역 상세 조회] reportId={}, userId={}", reportId, currentUserId);
        return new CommentReportResponse.DetailDTO(report);
    }
}