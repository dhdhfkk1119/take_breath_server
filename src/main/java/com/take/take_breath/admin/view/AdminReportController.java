package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.ReportListResponse;
import com.take.take_breath.community.comment_report.CommentReport;
import com.take.take_breath.community.community_report.CommunityReport;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view/reports")
public class AdminReportController {

    private final AdminReportService adminReportService;

    /**
     * 전체 신고 목록 (게시글 + 댓글 통합)
     */
    @GetMapping
    public String getAllReports(Model model) {
        Pageable pageable = PageRequest.of(0, 50, Sort.by("createdAt").descending());

        List<CommunityReport> postReports = adminReportService.getAllCommunityReports(pageable);
        List<CommentReport> commentReports = adminReportService.getAllCommentReports(pageable);

        List<ReportListResponse> reports = new ArrayList<>();
        reports.addAll(postReports.stream().map(r -> new ReportListResponse(r)).collect(Collectors.toList()));
        reports.addAll(commentReports.stream().map(r -> new ReportListResponse(r)).collect(Collectors.toList()));


        model.addAttribute("reports", reports);
        model.addAttribute("pageTitle", "전체 신고 관리");
        model.addAttribute("isAllFilter", true);
        model.addAttribute("isReports", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-reports.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin-reports.js"});
        return "admin/reports";
    }

    /**
     * 대기 중인 신고 목록
     */
    @GetMapping("/pending")
    public String getPendingReports(Model model) {
        Pageable pageable = PageRequest.of(0, 50, Sort.by("createdAt").descending());

        List<CommunityReport> postReports = adminReportService.getCommunityReportsByStatus(CommunityReportStatus.PENDING, pageable);
        List<CommentReport> commentReports = adminReportService.getCommentReportsByStatus(CommunityReportStatus.PENDING, pageable);

        List<ReportListResponse> reports = new ArrayList<>();
        reports.addAll(postReports.stream().map(r -> new ReportListResponse(r)).collect(Collectors.toList()));
        reports.addAll(commentReports.stream().map(r -> new ReportListResponse(r)).collect(Collectors.toList()));

        model.addAttribute("reports", reports);
        model.addAttribute("pageTitle", "대기 중인 신고");
        model.addAttribute("isPendingFilter", true);
        model.addAttribute("isReports", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-reports.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin-reports.js"});
        return "admin/reports";
    }

    /**
     * 승인된 신고 목록
     */
    @GetMapping("/approved")
    public String getApprovedReports(Model model) {
        Pageable pageable = PageRequest.of(0, 50, Sort.by("createdAt").descending());

        List<CommunityReport> postReports = adminReportService.getCommunityReportsByStatus(CommunityReportStatus.APPROVED, pageable);
        List<CommentReport> commentReports = adminReportService.getCommentReportsByStatus(CommunityReportStatus.APPROVED, pageable);

        List<ReportListResponse> reports = new ArrayList<>();
        reports.addAll(postReports.stream().map(r -> new ReportListResponse(r)).collect(Collectors.toList()));
        reports.addAll(commentReports.stream().map(r -> new ReportListResponse(r)).collect(Collectors.toList()));

        model.addAttribute("reports", reports);
        model.addAttribute("pageTitle", "승인된 신고");
        model.addAttribute("isApprovedFilter", true);
        model.addAttribute("isReports", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-reports.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin-reports.js"});
        return "admin/reports";
    }

    /**
     * 반려된 신고 목록
     */
    @GetMapping("/rejected")
    public String getRejectedReports(Model model) {
        Pageable pageable = PageRequest.of(0, 50, Sort.by("createdAt").descending());

        List<CommunityReport> postReports = adminReportService.getCommunityReportsByStatus(CommunityReportStatus.REJECTED, pageable);
        List<CommentReport> commentReports = adminReportService.getCommentReportsByStatus(CommunityReportStatus.REJECTED, pageable);

        List<ReportListResponse> reports = new ArrayList<>();
        reports.addAll(postReports.stream().map(r -> new ReportListResponse(r)).collect(Collectors.toList()));
        reports.addAll(commentReports.stream().map(r -> new ReportListResponse(r)).collect(Collectors.toList()));

        model.addAttribute("reports", reports);
        model.addAttribute("pageTitle", "반려된 신고");
        model.addAttribute("isRejectedFilter", true);
        model.addAttribute("isReports", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-reports.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin-reports.js"});
        return "admin/reports";
    }
}