package com.take.take_breath.admin.view;


import com.take.take_breath.admin.view.dto.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardStatsResponse stats = adminDashboardService.getDashboardStats();
        model.addAttribute("stats", stats);

        // 레이아웃 설정
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
