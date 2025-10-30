package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.CounselorListDTO;
import com.take.take_breath.counselor.Counselor;
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
        List<CounselorListDTO> counselorList = adminCounselorService.findPendingCounselors()
                .stream()
                .map(counselor -> new CounselorListDTO(counselor))
                .toList();

        model.addAttribute("counselors", counselorList);
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