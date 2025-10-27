package com.take.take_breath.community.comment_report_process;

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
@RequestMapping("/api/admin/comment/reports")
@Slf4j
public class CommentReportProcessController {

    private final CommentReportProcessService processService;

    /**
     * 신고 처리 상태 업데이트 (관리자 전용)
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.ADMIN}) 추가
     */
    @PostMapping("/{reportId}/status")
    public ResponseEntity<ApiUtil.ApiResult<CommentReportProcessResponse.ProcessDTO>> updateStatus(
            @PathVariable Long reportId,
            @Valid @RequestBody CommentReportProcessRequest.UpdateStatusDTO updateStatusDTO,
            @RequestParam Long adminId) {  // TODO: JWT에서 추출로 변경

        CommentReportProcessResponse.ProcessDTO response = processService.updateStatus(reportId, adminId, updateStatusDTO);
        log.info("[신고 처리] processId={}, reportId={}, status={}", response.getProcessId(), reportId, updateStatusDTO.getStatus());
        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 전체 신고 목록 조회 (관리자 전용)
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.ADMIN}) 추가
     */
    @GetMapping
    public ResponseEntity<ApiUtil.ApiResult<List<CommentReportProcessResponse.ListDTO>>> findAllReports(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        List<CommentReportProcessResponse.ListDTO> reports = processService.findAllReports(pageable);
        log.info("[관리자 신고 목록 조회] count={}", reports.size());
        return ResponseEntity.ok(ApiUtil.success(reports));
    }

    /**
     * 신고 상세 조회 (관리자 전용)
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.ADMIN}) 추가
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiUtil.ApiResult<CommentReportProcessResponse.DetailDTO>> getReportDetail(
            @PathVariable Long reportId) {

        CommentReportProcessResponse.DetailDTO report = processService.detail(reportId);
        log.info("[관리자 신고 상세 조회] reportId={}", reportId);
        return ResponseEntity.ok(ApiUtil.success(report));
    }
}