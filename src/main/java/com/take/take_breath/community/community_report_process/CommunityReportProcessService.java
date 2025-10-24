package com.take.take_breath.community.community_report_process;

import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.community.community_report.CommunityReport;
import com.take.take_breath.community.community_report.CommunityReportRepository;
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
public class CommunityReportProcessService {

    private final CommunityReportRepository reportRepository;
    private final CommunityReportProcessRepository processRepository;

    /**
     * 신고 처리 상태 업데이트 (관리자 전용)
     */
    @Transactional
    public CommunityReportProcessResponse.ProcessDTO updateStatus(Long reportId, Long adminId,
                                                                  CommunityReportProcessRequest.UpdateStatusDTO updateStatusDTO) {
        CommunityReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다. ID: " + reportId));

        if (report.getStatus() != CommunityReportStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신고입니다. 현재 상태: " + report.getStatus());
        }

        report.setStatus(updateStatusDTO.getStatus());

        CommunityReportProcess process = CommunityReportProcess.builder()
                .report(report)
                .adminId(adminId)
                .status(updateStatusDTO.getStatus())
                .adminComment(updateStatusDTO.getAdminComment())
                .build();

        CommunityReportProcess savedProcess = processRepository.save(process);

        // 신고 승인 시 게시글 처리
        if (updateStatusDTO.getStatus() == CommunityReportStatus.APPROVED) {
            CommunityPost post = report.getPost();
            post.softDelete();

            // 같은 게시글의 다른 PENDING 신고도 모두 APPROVED로 변경
            List<CommunityReport> otherPendingReports = reportRepository
                    .findByPostIdAndStatus(post.getId(), CommunityReportStatus.PENDING);

            otherPendingReports.stream()
                    .filter(r -> !r.getId().equals(reportId))  // 현재 처리중인 신고 제외
                    .forEach(r -> {
                        r.setStatus(CommunityReportStatus.APPROVED);

                        CommunityReportProcess autoProcess = CommunityReportProcess.builder()
                                .report(r)
                                .adminId(adminId)
                                .status(CommunityReportStatus.APPROVED)
                                .adminComment("동일 게시글 신고 승인으로 인한 자동 처리")
                                .build();
                        processRepository.save(autoProcess);
                    });

            log.warn("[신고 승인 - 게시글 삭제] postId={}, reportId={}, 자동 처리된 추가 신고 수={}",
                    post.getId(), reportId, otherPendingReports.size() - 1);
        }

        log.info("[신고 처리] processId={}, reportId={}, status={}, adminId={}",
                savedProcess.getId(), reportId, updateStatusDTO.getStatus(), adminId);

        return CommunityReportProcessResponse.ProcessDTO.builder()
                .process(savedProcess)
                .build();
    }

    /**
     * 전체 신고 목록 조회 (관리자 전용)
     */
    public List<CommunityReportProcessResponse.ListDTO> findAllReports(Pageable pageable) {
        Page<CommunityReport> reports = reportRepository.findAll(pageable);

        return reports.stream()
                .map(report -> {
                    CommunityPost post = report.getPost();
                    if (post.isDeleted()) {
                        return new CommunityReportProcessResponse.ListDTO(
                                report,
                                "(삭제됨) " + post.getTitle()
                        );
                    }
                    return new CommunityReportProcessResponse.ListDTO(report);
                })
                .collect(Collectors.toList());
    }

    /**
     * 신고 상세 조회 (관리자 전용)
     */
    public CommunityReportProcessResponse.DetailDTO detail(Long reportId) {
        CommunityReport report = reportRepository.findByIdWithAdminComments(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다. ID: " + reportId));

        log.info("[관리자 신고 상세 조회] reportId={}", reportId);

        if (report.getPost().isDeleted()) {
            return CommunityReportProcessResponse.DetailDTO.builder()
                    .report(report)
                    .deletedPostTitle("(삭제됨) " + report.getPost().getTitle())
                    .build();
        }

        return CommunityReportProcessResponse.DetailDTO.builder()
                .report(report)
                .build();
    }
}