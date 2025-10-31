package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.CounselorListResponse;
import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.members.Status;
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

    // 상담사 관리 메인
    @GetMapping
    public String listRedirect() {
        return "redirect:/api/admin/view/counselors/list";
    }

    // 전체 상담사 목록
    @GetMapping("/list")
    public String getAllCounselors(Model model) {
        List<CounselorListResponse> counselors = adminCounselorService.getAllCounselors()
                .stream()
                .map(c -> new CounselorListResponse(c))
                .toList();

        model.addAttribute("counselors", counselors);
        model.addAttribute("pageTitle", "전체 상담사 목록");
        model.addAttribute("isAllFilter", true);
        model.addAttribute("isCounselors", true);
        return "admin/counselor-list";
    }

    // 활성 상담사 목록
    @GetMapping("/active")
    public String getActiveCounselors(Model model) {
        List<CounselorListResponse> counselors = adminCounselorService.getCounselorsByStatus(Status.ACTIVE)
                .stream()
                .map(c -> new CounselorListResponse(c))
                .toList();

        model.addAttribute("counselors", counselors);
        model.addAttribute("pageTitle", "활성 상담사 목록");
        model.addAttribute("isActiveFilter", true);
        model.addAttribute("isCounselors", true);
        return "admin/counselor-list";
    }

    // 승인 대기 상담사 목록
    @GetMapping("/pending")
    public String getPendingCounselors(Model model) {
        List<CounselorListResponse> counselors = adminCounselorService.getCounselorsByStatus(Status.PENDING)
                .stream()
                .map(c -> new CounselorListResponse(c))
                .toList();

        model.addAttribute("counselors", counselors);
        model.addAttribute("pageTitle", "승인 대기 상담사 목록");
        model.addAttribute("isPendingFilter", true);
        model.addAttribute("isCounselors", true);
        return "admin/counselor-list";
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

    // 상담사 거절
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