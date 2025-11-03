package com.take.take_breath.community.comment_report_process;

import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath.community.comment_report.CommentReport;
import com.take.take_breath.community.comment_report.CommentReportRepository;
import com.take.take_breath.community.community_comment.CommunityComment;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentReportProcessService {

    private final CommentReportRepository reportRepository;
    private final CommentReportProcessRepository processRepository;
    private final MemberRepository memberRepository;

    /**
     * 관리자 이메일로 관리자 ID 조회
     * (세션 기반 환경용 추가 메서드)
     */
    public Long findAdminIdByEmail(String email) {
        Member admin = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception404("관리자 계정을 찾을 수 없습니다."));
        return admin.getId();
    }

    /**
     * 신고 처리 상태 업데이트 (관리자 전용 - 세션 기반)
     */
    @Transactional
    public CommentReportProcessResponse.ProcessDTO updateStatus(
            Long reportId,
            Long adminId,
            CommentReportProcessRequest.UpdateStatusDTO updateStatusDTO
    ) {
        CommentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다. ID: " + reportId));

        if (report.getStatus() != CommunityReportStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신고입니다. 현재 상태: " + report.getStatus());
        }

        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new Exception404("관리자를 찾을 수 없습니다."));

        // 상태 변경
        report.setStatus(updateStatusDTO.getStatus());

        CommentReportProcess process = CommentReportProcess.builder()
                .report(report)
                .admin(admin)
                .status(updateStatusDTO.getStatus())
                .adminComment(updateStatusDTO.getAdminComment())
                .build();

        CommentReportProcess savedProcess = processRepository.save(process);

        // 신고 승인 시 댓글 삭제 및 관련 신고 자동 처리
        if (updateStatusDTO.getStatus() == CommunityReportStatus.APPROVED) {
            CommunityComment comment = report.getComment();
            comment.softDelete();

            List<CommentReport> otherPendingReports =
                    reportRepository.findByCommentIdAndStatus(comment.getId(), CommunityReportStatus.PENDING);

            otherPendingReports.stream()
                    .filter(r -> !r.getId().equals(reportId))
                    .forEach(r -> {
                        r.setStatus(CommunityReportStatus.APPROVED);

                        CommentReportProcess autoProcess = CommentReportProcess.builder()
                                .report(r)
                                .admin(admin)
                                .status(CommunityReportStatus.APPROVED)
                                .adminComment("동일 댓글 신고 승인으로 인한 자동 처리")
                                .build();
                        processRepository.save(autoProcess);
                    });

            log.warn("[신고 승인 - 댓글 삭제] commentId={}, reportId={}, 자동 처리된 추가 신고 수={}",
                    comment.getId(), reportId, otherPendingReports.size() - 1);

            // [신고 승인 시 제재 체크 로직 추가]
            Member targetMember = comment.getMember(); // 댓글 작성자
            long approvedCount = reportRepository.countDistinctByCommentMemberAndStatus(
                    targetMember,
                    CommunityReportStatus.APPROVED
            );

            if (approvedCount >= 3) {
                targetMember.setStatus(Status.SUSPENDED);
                targetMember.setSuspendedUntil(LocalDateTime.now().plusMinutes(1)); // 테스트용 1분 정지
                memberRepository.save(targetMember);

                log.warn("[자동 정지] memberId={} - 댓글 신고 승인 누적 {}건 → 1분 정지 적용",
                        targetMember.getId(), approvedCount);
            }
        }

        log.info("[신고 처리 완료] processId={}, reportId={}, status={}, adminId={}",
                savedProcess.getId(), reportId, updateStatusDTO.getStatus(), adminId);

        return CommentReportProcessResponse.ProcessDTO.builder()
                .process(savedProcess)
                .build();
    }

    /**
     * 전체 신고 목록 조회 (관리자 전용)
     */
    public List<CommentReportProcessResponse.ListDTO> findAllReports(Pageable pageable) {
        Page<CommentReport> reports = reportRepository.findAll(pageable);

        return reports.stream()
                .map(report -> {
                    CommunityComment comment = report.getComment();
                    if (comment.isDeleted()) {
                        return new CommentReportProcessResponse.ListDTO(
                                report,
                                "(삭제된 댓글)"
                        );
                    }
                    return new CommentReportProcessResponse.ListDTO(report);
                })
                .collect(Collectors.toList());
    }

    /**
     * 신고 상세 조회 (관리자 전용)
     */
    public CommentReportProcessResponse.DetailDTO detail(Long reportId) {
        CommentReport report = reportRepository.findByIdWithAdminComments(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다. ID: " + reportId));

        log.info("[관리자 댓글 신고 상세 조회] reportId={}", reportId);

        if (report.getComment().isDeleted()) {
            return CommentReportProcessResponse.DetailDTO.builder()
                    .report(report)
                    .deletedCommentContent("(삭제된 댓글)")
                    .build();
        }

        return CommentReportProcessResponse.DetailDTO.builder()
                .report(report)
                .build();
    }
}
