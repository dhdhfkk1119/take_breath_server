package com.take.take_breath.community.community_report;

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
@RequestMapping("/api/community/reports")
@Slf4j
public class CommunityReportController {

    private final CommunityReportService reportService;

    /**
     * 게시글 신고
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.USER}) 추가
     */
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityReportResponse.CreateDTO>> reportPost(
            @PathVariable Long postId,
            @Valid @RequestBody CommunityReportRequest.CreateDTO createDTO,
            @RequestParam Long userId) {  // TODO: JWT에서 추출로 변경

        CommunityReportResponse.CreateDTO response = reportService.createReport(postId, userId, createDTO);
        log.info("[게시글 신고] reportId={}, postId={}, userId={}", response.getId(), postId, userId);
        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 내 신고 내역 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiUtil.ApiResult<List<CommunityReportResponse.ListDTO>>> findMyReports(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Long userId) {

        List<CommunityReportResponse.ListDTO> reports = reportService.findAllMyReports(userId, pageable);
        log.info("[내 신고 내역 조회] userId={}, count={}", userId, reports.size());
        return ResponseEntity.ok(ApiUtil.success(reports));
    }

    /**
     * 신고 내역 상세 조회
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityReportResponse.DetailDTO>> getReportDetail(
            @PathVariable Long reportId,
            @RequestParam Long userId) {

        CommunityReportResponse.DetailDTO reportDetail = reportService.detail(reportId, userId);
        log.info("[신고 내역 상세 조회] reportId={}, userId={}", reportId, userId);
        return ResponseEntity.ok(ApiUtil.success(reportDetail));
    }
}