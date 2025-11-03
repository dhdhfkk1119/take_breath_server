package com.take.take_breath.community.comment_report_process;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/comment/reports")
@Slf4j
public class CommentReportProcessController {

    private final CommentReportProcessService processService;

    /**
     * 신고 처리 상태 업데이트 (관리자 세션 기반)
     * Mustache 관리자 페이지의 JS(fetch)에서 호출됨
     */
    @PostMapping("/{reportId}/status")
    @ResponseBody
    public ResponseEntity<ApiUtil.ApiResult<CommentReportProcessResponse.ProcessDTO>> updateStatus(
            @PathVariable Long reportId,
            @Valid @RequestBody CommentReportProcessRequest.UpdateStatusDTO updateStatusDTO,
            HttpSession session) {

        // 세션 기반 관리자 인증 확인
        if (session == null || session.getAttribute("adminToken") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiUtil.fail("관리자 인증이 필요합니다.", HttpStatus.UNAUTHORIZED));
        }

        String adminEmail = (String) session.getAttribute("adminEmail");
        Long adminId = processService.findAdminIdByEmail(adminEmail); // ⚙️ 필요 시 구현

        CommentReportProcessResponse.ProcessDTO response =
                processService.updateStatus(reportId, adminId, updateStatusDTO);

        log.info("[관리자 댓글 신고 처리] reportId={}, status={}, admin={}", reportId, updateStatusDTO.getStatus(), adminEmail);
        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 전체 신고 목록 조회
     * Mustache 템플릿 렌더링용
     */
    @GetMapping
    public String findAllReports(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpSession session,
            org.springframework.ui.Model model) {

        if (session == null || session.getAttribute("adminToken") == null) {
            return "redirect:/api/admin/view/login";
        }

        List<CommentReportProcessResponse.ListDTO> reports = processService.findAllReports(pageable);
        model.addAttribute("reports", reports);
        model.addAttribute("pageTitle", "댓글 신고 관리");

        log.info("[SSR 관리자 댓글 신고 목록 조회] count={}", reports.size());
        return "admin/comment-report-list"; // ⚙️ Mustache 템플릿 파일명 (admin/comment-report-list.mustache)
    }

    /**
     * 신고 상세 조회
     */
    @GetMapping("/{reportId}")
    public String getReportDetail(
            @PathVariable Long reportId,
            HttpSession session,
            org.springframework.ui.Model model) {

        if (session == null || session.getAttribute("adminToken") == null) {
            return "redirect:/api/admin/view/login";
        }

        CommentReportProcessResponse.DetailDTO report = processService.detail(reportId);
        model.addAttribute("report", report);
        model.addAttribute("pageTitle", "댓글 신고 상세");

        log.info("[SSR 관리자 댓글 신고 상세 조회] reportId={}", reportId);
        return "admin/comment-report-detail"; // ⚙️ Mustache 템플릿 파일명
    }
}