package com.take.take_breath.admin.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.take.take_breath.admin.view.dto.DashboardStatsResponse;
import com.take.take_breath.admin.view.dto.MemberGrowthData;
import com.take.take_breath.admin.view.dto.ReportProcessData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/dashboard")
    public String dashboard(Model model) throws Exception {
        DashboardStatsResponse stats = adminDashboardService.getDashboardStats();

        // 회원 증가 데이터 포함
        stats.setMemberGrowthData(adminDashboardService.getMemberGrowthData(6));

        // 신고처리
        ReportProcessData reportProcessData = adminDashboardService.getReportProcessData(6);
        stats.setReportProcessData(reportProcessData);

        // 신고 상태별
        Map<String, Integer> reportStatusData = adminDashboardService.getReportStatusStats();
        stats.setReportStatusData(reportStatusData);

        // JSON으로 직렬화
        String statsJson = objectMapper.writeValueAsString(stats);

        model.addAttribute("stats", stats);
        model.addAttribute("statsJson", statsJson);

        model.addAttribute("pageTitle", "관리자 대시보드");
        model.addAttribute("isDashboard", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-dashboard.css"});
        model.addAttribute("additionalScript", new String[]{
                "https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.0/chart.umd.min.js"
        });
        model.addAttribute("scripts", new String[]{"/js/admin-dashboard.js"});

        return "admin/dashboard";
    }
}