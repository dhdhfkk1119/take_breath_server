package com.take.take_breath.community.comment_report;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath.community.community_comment.CommunityComment;
import com.take.take_breath.community.community_comment.CommunityCommentRepository;
import com.take.take_breath.community.community_report.CommunityReportStatus;
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
public class CommentReportService {

    private final CommentReportRepository reportRepository;
    private final CommunityCommentRepository commentRepository;
    private final MemberRepository memberRepository;

    /**
     * 댓글 신고
     */
    @Transactional
    public CommentReportResponse.CreateDTO createReport(Long commentId, Long currentMemberId,
                                                        CommentReportRequest.CreateDTO createDTO) {

        if (reportRepository.existsByReporterIdAndCommentId(currentMemberId, commentId)) {
            throw new Exception400("이미 신고한 댓글입니다.");
        }

        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new Exception404("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (comment.isDeleted()) {
            throw new Exception400("삭제된 댓글은 신고할 수 없습니다.");
        }

        Member reporter = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new Exception404("회원을 찾을 수 없습니다."));

        CommentReport report = CommentReport.builder()
                .comment(comment)
                .reporter(reporter)
                .reason(createDTO.getReason())
                .status(CommunityReportStatus.PENDING)
                .build();

        CommentReport savedReport = reportRepository.save(report);

        // 댓글 신고 횟수 증가
        comment.increaseReportCount();

        log.info("[댓글 신고] reportId={}, commentId={}, reporterId={}", savedReport.getId(), commentId, currentMemberId);
        return CommentReportResponse.CreateDTO.builder()
                .report(savedReport)
                .message("신고가 접수되었습니다.")
                .build();
    }

    /**
     * 내 신고 내역 목록 조회
     */
    public List<CommentReportResponse.ListDTO> findAllMyReports(Long currentMemberId, Pageable pageable) {
        Page<CommentReport> reports = reportRepository.findAllByReporterIdOrderByCreatedAtDesc(currentMemberId, pageable);
        return reports.stream()
                .map(report -> new CommentReportResponse.ListDTO(report))
                .collect(Collectors.toList());
    }

    /**
     * 신고 내역 상세 조회
     */
    public CommentReportResponse.DetailDTO detail(Long reportId, Long currentMemberId) {
        CommentReport report = reportRepository.findByIdWithAdminComments(reportId)
                .orElseThrow(() -> new Exception404("신고 내역을 찾을 수 없습니다. ID: " + reportId));

        if (!report.isOwner(currentMemberId)) {
            throw new Exception403("본인이 신고한 내역만 조회할 수 있습니다.");
        }

        log.info("[신고 내역 상세 조회] reportId={}, memberId={}", reportId, currentMemberId);
        return new CommentReportResponse.DetailDTO(report);
    }
}