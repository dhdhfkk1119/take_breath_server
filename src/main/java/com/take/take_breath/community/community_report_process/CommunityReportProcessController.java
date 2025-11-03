package com.take.take_breath.community.community_report_process;

import com.take.take_breath._core._utils.ApiUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/community/reports")
@Slf4j
public class CommunityReportProcessController {

    private final CommunityReportProcessService processService;

    /**
     * 신고 처리 상태 업데이트 (관리자 세션 기반)
     * Mustache 관리자 페이지의 JS(fetch)에서 호출됨
     */
    @PostMapping("/{reportId}/status")
    @ResponseBody
    public ResponseEntity<ApiUtil.ApiResult<CommunityReportProcessResponse.ProcessDTO>> updateStatus(
            @PathVariable Long reportId,
            @Valid @RequestBody CommunityReportProcessRequest.UpdateStatusDTO updateStatusDTO,
            HttpSession session) {

        // 세션 인증 확인
        if (session == null || session.getAttribute("adminToken") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiUtil.fail("관리자 인증이 필요합니다.", HttpStatus.UNAUTHORIZED));
        }

        String adminEmail = (String) session.getAttribute("adminEmail");
        Long adminId = processService.findAdminIdByEmail(adminEmail); // 서비스 내부에서 이메일 기반 관리자 조회

        CommunityReportProcessResponse.ProcessDTO response =
                processService.updateStatus(reportId, adminId, updateStatusDTO);

        log.info("[관리자 게시글 신고 처리] reportId={}, status={}, admin={}", reportId, updateStatusDTO.getStatus(), adminEmail);
        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 전체 신고 목록 조회 (SSR)
     * Mustache에서 관리자용 테이블로 렌더링
     */
    @GetMapping
    public String findAllReports(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpSession session,
            Model model) {

        if (session == null || session.getAttribute("adminToken") == null) {
            return "redirect:/api/admin/view/login";
        }

        List<CommunityReportProcessResponse.ListDTO> reports = processService.findAllReports(pageable);
        model.addAttribute("reports", reports);
        model.addAttribute("pageTitle", "게시글 신고 관리");

        log.info("[SSR 관리자 게시글 신고 목록 조회] count={}", reports.size());
        return "admin/community-report-list"; // ⚙️ Mustache 파일명 (예: admin/community-report-list.mustache)
    }

    /**
     * 신고 상세 조회 (SSR)
     */
    @GetMapping("/{reportId}")
    public String getReportDetail(
            @PathVariable Long reportId,
            HttpSession session,
            Model model) {

        if (session == null || session.getAttribute("adminToken") == null) {
            return "redirect:/api/admin/view/login";
        }

        CommunityReportProcessResponse.DetailDTO report = processService.detail(reportId);
        model.addAttribute("report", report);
        model.addAttribute("pageTitle", "게시글 신고 상세");

        log.info("[SSR 관리자 게시글 신고 상세 조회] reportId={}", reportId);
        return "admin/community-report-detail"; // ⚙️ Mustache 파일명 (예: admin/community-report-detail.mustache)
    }
}
