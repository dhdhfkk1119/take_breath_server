package com.take.take_breath.admin.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.take.take_breath.admin.view.dto.DashboardStatsResponse;
import com.take.take_breath.admin.view.dto.MemberGrowthData;
import com.take.take_breath.admin.view.dto.ReportProcessData;
import com.take.take_breath.payment.PaymentResponse;
import com.take.take_breath.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/dashboard")
    public String dashboard(Model model) throws Exception {
        // 기본 대시보드 통계
        DashboardStatsResponse stats = adminDashboardService.getDashboardStats();

        // 회원 증가 데이터
        stats.setMemberGrowthData(adminDashboardService.getMemberGrowthData(6));

        // 신고 처리 현황
        ReportProcessData reportProcessData = adminDashboardService.getReportProcessData(6);
        stats.setReportProcessData(reportProcessData);

        // 신고 상태별 건수
        Map<String, Integer> reportStatusData = adminDashboardService.getReportStatusStats();
        stats.setReportStatusData(reportStatusData);

        // 결제 수수료 통계 (월별)
        List<PaymentResponse.AdminFeeStatsDTO> monthlyStats = paymentService.getMonthlyFeeStats();

        // JSON 직렬화 → JS에서 window.stats로 접근 가능
        String statsJson = objectMapper.writeValueAsString(stats);

        // Model 전달
        model.addAttribute("stats", stats);
        model.addAttribute("statsJson", statsJson);
        model.addAttribute("monthlyStats", monthlyStats);
        model.addAttribute("pageTitle", "관리자 대시보드");
        model.addAttribute("isDashboard", true);

        // CSS & JS
        model.addAttribute("additionalCss", new String[]{
                "/css/admin-dashboard.css",
                "/css/admin-common.css"
        });
        model.addAttribute("additionalScript", new String[]{
                "https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.0/chart.umd.min.js"
        });
        model.addAttribute("scripts", new String[]{
                "/js/admin-dashboard.js"
        });

        return "admin/dashboard";
    }
}
