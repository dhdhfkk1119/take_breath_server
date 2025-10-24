package com.take.take_breath.community.comment_report;

import com.take.take_breath._core._utils.ApiUtil;
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
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.USER}) 추가
     */
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<ApiUtil.ApiResult<CommentReportResponse.CreateDTO>> reportComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentReportRequest.CreateDTO createDTO,
            @RequestParam Long userId) {  // TODO: JWT에서 추출로 변경

        CommentReportResponse.CreateDTO response = reportService.createReport(commentId, userId, createDTO);
        log.info("[댓글 신고] reportId={}, commentId={}, userId={}", response.getId(), commentId, userId);
        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 내 신고 내역 목록 조회
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.USER}) 추가
     */
    @GetMapping
    public ResponseEntity<ApiUtil.ApiResult<List<CommentReportResponse.ListDTO>>> findMyReports(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Long userId) {  // TODO: JWT에서 추출로 변경

        List<CommentReportResponse.ListDTO> reports = reportService.findAllMyReports(userId, pageable);
        log.info("[내 신고 내역 조회] userId={}, count={}", userId, reports.size());
        return ResponseEntity.ok(ApiUtil.success(reports));
    }

    /**
     * 신고 내역 상세 조회
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.USER}) 추가
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiUtil.ApiResult<CommentReportResponse.DetailDTO>> getReportDetail(
            @PathVariable Long reportId,
            @RequestParam Long userId) {  // TODO: JWT에서 추출로 변경

        CommentReportResponse.DetailDTO reportDetail = reportService.detail(reportId, userId);
        log.info("[신고 내역 상세 조회] reportId={}, userId={}", reportId, userId);
        return ResponseEntity.ok(ApiUtil.success(reportDetail));
    }
}