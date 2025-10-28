package com.take.take_breath.community.comment_report;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/comment-reports")
@Slf4j
public class CommentReportController {

    private final CommentReportService reportService;

    /**
     * 댓글 신고
     */
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<ApiUtil.ApiResult<CommentReportResponse.CreateDTO>> reportComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentReportRequest.CreateDTO createDTO,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        CommentReportResponse.CreateDTO response = reportService.createReport(commentId, memberId, createDTO);
        log.info("[댓글 신고] reportId={}, commentId={}, memberId={}", response.getId(), commentId, memberId);
        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 내 신고 내역 목록 조회
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping
    public ResponseEntity<ApiUtil.ApiResult<List<CommentReportResponse.ListDTO>>> findMyReports(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        List<CommentReportResponse.ListDTO> reports = reportService.findAllMyReports(memberId, pageable);
        log.info("[내 신고 내역 조회] memberId={}, count={}", memberId, reports.size());
        return ResponseEntity.ok(ApiUtil.success(reports));
    }

    /**
     * 신고 내역 상세 조회
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiUtil.ApiResult<CommentReportResponse.DetailDTO>> getReportDetail(
            @PathVariable Long reportId,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        CommentReportResponse.DetailDTO reportDetail = reportService.detail(reportId, memberId);
        log.info("[신고 내역 상세 조회] reportId={}, memberId={}", reportId, memberId);
        return ResponseEntity.ok(ApiUtil.success(reportDetail));
    }
}