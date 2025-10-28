package com.take.take_breath.community.community_report;

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
@RequestMapping("/api/community/reports")
@Slf4j
public class CommunityReportController {

    private final CommunityReportService reportService;

    /**
     * 게시글 신고
     */
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityReportResponse.CreateDTO>> reportPost(
            @PathVariable Long postId,
            @Valid @RequestBody CommunityReportRequest.CreateDTO createDTO,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        CommunityReportResponse.CreateDTO response = reportService.createReport(postId, memberId, createDTO);
        log.info("[게시글 신고] reportId={}, postId={}, memberId={}", response.getId(), postId, memberId);
        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 내 신고 내역 목록 조회
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping
    public ResponseEntity<ApiUtil.ApiResult<List<CommunityReportResponse.ListDTO>>> findMyReports(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        List<CommunityReportResponse.ListDTO> reports = reportService.findAllMyReports(memberId, pageable);
        log.info("[내 신고 내역 조회] memberId={}, count={}", memberId, reports.size());
        return ResponseEntity.ok(ApiUtil.success(reports));
    }

    /**
     * 신고 내역 상세 조회
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityReportResponse.DetailDTO>> getReportDetail(
            @PathVariable Long reportId,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        CommunityReportResponse.DetailDTO reportDetail = reportService.detail(reportId, memberId);
        log.info("[신고 내역 상세 조회] reportId={}, memberId={}", reportId, memberId);
        return ResponseEntity.ok(ApiUtil.success(reportDetail));
    }
}