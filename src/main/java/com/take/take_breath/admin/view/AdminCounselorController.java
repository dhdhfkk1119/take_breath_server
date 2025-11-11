package com.take.take_breath.admin.view;

import com.take.take_breath._core._utils.PageUtil.PageResponse;
import com.take.take_breath.admin.view.dto.CounselorListResponse;
import com.take.take_breath.members.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view/counselors")
public class AdminCounselorController {

    private final AdminCounselorService adminCounselorService;

    @GetMapping
    public String listRedirect() {
        return "redirect:/api/admin/view/counselors/list";
    }

    // 전체 상담사 목록 (페이징 적용)
    @GetMapping("/list")
    public String getAllCounselors(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model
    ) {
        PageResponse<CounselorListResponse> pageResponse = adminCounselorService.getAllCounselors(pageable);

        int currentPage = pageResponse.getPageNumber() + 1;
        int totalPage = pageResponse.getTotalPages();

        int nextPage = pageResponse.getPageNumber() + 1;
        int prevPage = pageResponse.getPageNumber() - 1;

        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPage, currentPage + 2);

        if (endPage - startPage < 4) {
            if (startPage == 1) {
                endPage = Math.min(totalPage, startPage + 4);
            } else if (endPage == totalPage) {
                startPage = Math.max(1, endPage - 4);
            }
        }

        // 페이지 번호 리스트 생성
        List<Map<String, Object>> pageNumbers = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("pageNum", i);
            pageInfo.put("pageIndex", i - 1); // 0-based index for URL
            pageInfo.put("isCurrent", i == currentPage);
            pageNumbers.add(pageInfo);
        }

        model.addAttribute("pageInfo", pageResponse);
        model.addAttribute("counselors", pageResponse.getContent());

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("pageNumbers", pageNumbers);

        model.addAttribute("pageTitle", "전체 상담사 목록");
        model.addAttribute("isAllFilter", true);
        model.addAttribute("isCounselors", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-counselors.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin-counselors.js"});
        return "admin/counselor-list";
    }

    // 활성 상담사 목록 (페이징 적용)
    @GetMapping("/active")
    public String getActiveCounselors(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model
    ) {
        PageResponse<CounselorListResponse> pageResponse =
                adminCounselorService.getCounselorsByStatus(Status.ACTIVE, pageable);

        int currentPage = pageResponse.getPageNumber() + 1;
        int totalPage = pageResponse.getTotalPages();

        int nextPage = pageResponse.getPageNumber() + 1;
        int prevPage = pageResponse.getPageNumber() - 1;

        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPage, currentPage + 2);

        if (endPage - startPage < 4) {
            if (startPage == 1) {
                endPage = Math.min(totalPage, startPage + 4);
            } else if (endPage == totalPage) {
                startPage = Math.max(1, endPage - 4);
            }
        }

        // 페이지 번호 리스트 생성
        List<Map<String, Object>> pageNumbers = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("pageNum", i);
            pageInfo.put("pageIndex", i - 1);
            pageInfo.put("isCurrent", i == currentPage);
            pageNumbers.add(pageInfo);
        }

        model.addAttribute("pageInfo", pageResponse);
        model.addAttribute("counselors", pageResponse.getContent());

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("pageNumbers", pageNumbers);

        model.addAttribute("pageTitle", "활성 상담사 목록");
        model.addAttribute("isActiveFilter", true);
        model.addAttribute("isCounselors", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-counselors.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin-counselors.js"});
        return "admin/counselor-list";
    }

    // 승인 대기 상담사 목록 (페이징 적용)
    @GetMapping("/pending")
    public String getPendingCounselors(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model
    ) {
        PageResponse<CounselorListResponse> pageResponse =
                adminCounselorService.getCounselorsByStatus(Status.PENDING, pageable);

        int currentPage = pageResponse.getPageNumber() + 1;
        int totalPage = pageResponse.getTotalPages();

        int nextPage = pageResponse.getPageNumber() + 1;
        int prevPage = pageResponse.getPageNumber() - 1;

        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPage, currentPage + 2);

        if (endPage - startPage < 4) {
            if (startPage == 1) {
                endPage = Math.min(totalPage, startPage + 4);
            } else if (endPage == totalPage) {
                startPage = Math.max(1, endPage - 4);
            }
        }

        // 페이지 번호 리스트 생성
        List<Map<String, Object>> pageNumbers = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("pageNum", i);
            pageInfo.put("pageIndex", i - 1);
            pageInfo.put("isCurrent", i == currentPage);
            pageNumbers.add(pageInfo);
        }

        model.addAttribute("pageInfo", pageResponse);
        model.addAttribute("counselors", pageResponse.getContent());

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("pageNumbers", pageNumbers);

        model.addAttribute("pageTitle", "승인 대기 상담사 목록");
        model.addAttribute("isPendingFilter", true);
        model.addAttribute("isCounselors", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-counselors.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin-counselors.js"});
        return "admin/counselor-list";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminCounselorService.approveCounselor(id);
            redirectAttributes.addFlashAttribute("message", "상담사 승인이 완료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/api/admin/view/counselors/pending";
    }

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
        return "redirect:/api/admin/view/counselors/pending";
    }
}