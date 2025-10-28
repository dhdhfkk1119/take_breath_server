package com.take.take_breath.admin;

import org.springframework.ui.Model;
import com.take.take_breath.counselor.CounselorApproval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    // 승인 대기중 상담사 목록
    @GetMapping("/counselors")
    public String getPendingCounselors(Model model) {
        List<CounselorApproval> approvals = adminService.getPendingApprovals();
        model.addAttribute("approvals", approvals);
        return "admin/counselor-list";
    }

    // 승인
    @PostMapping("/counselors/{id}/approve")
    public String approveCounselor(@PathVariable("id") Long id) {
        adminService.approveCounselor(id);
        return "redirect:/admin/counselors";
    }

    // 거절
    @PostMapping("/counselors/{id}/reject")
    public String rejectCounselor(
            @PathVariable("id") Long id,
            @RequestParam("reason") String reason
    ) {
        adminService.rejectCounselor(id, reason);
        return "redirect:/admin/counselors";
    }
}
