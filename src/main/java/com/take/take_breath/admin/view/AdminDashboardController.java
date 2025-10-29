package com.take.take_breath.admin.view;


import com.take.take_breath.admin.view.dto.DashboardStatsDTO;
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
        DashboardStatsDTO stats = adminDashboardService.getDashboardStats();
        model.addAttribute("stats", stats);
        return "admin/dashboard";
    }
}
