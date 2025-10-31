package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.CounselorListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view/counselors")
public class AdminCounselorController {

    private final AdminCounselorService adminCounselorService;

    // 상담사 목록 (승인 대기 중)
    @GetMapping
    public String list(Model model) {
        List<CounselorListResponse> counselorList = adminCounselorService.findPendingCounselors()
                .stream()
                .map(counselor -> new CounselorListResponse(counselor))
                .toList();

        // 레이아웃 설정
        model.addAttribute("counselors", counselorList);
        model.addAttribute("pageTitle", "상담사 관리");
        model.addAttribute("isCounselors", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-counselors.css"});
        model.addAttribute("scripts", new String[]{"/js/admin-counselors.js"});
        return "admin/counselors";
    }

    // 상담사 승인
    @PostMapping("/{id}/approve")
    public String approve(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminCounselorService.approveCounselor(id);
            redirectAttributes.addFlashAttribute("message", "상담사 승인이 완료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/api/admin/view/counselors";
    }

    // 상담사 거절 (옵션)
    @PostMapping("/{id}/reject")
    public String reject(
            @PathVariable Long id,
            @RequestParam String rejectReason,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminCounselorService.rejectCounselor(id);
            redirectAttributes.addFlashAttribute("message", "상담사 신청이 거절되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/api/admin/view/counselors";
    }
}