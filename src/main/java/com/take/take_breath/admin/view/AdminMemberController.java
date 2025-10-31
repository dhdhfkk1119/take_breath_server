package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.MemberListResponse;
import com.take.take_breath.members.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    // 대시보드
    @GetMapping
    public String listRedirect() {
        return "redirect:/api/admin/view/members/list";
    }

    // 전체 회원 목록
    @GetMapping("/list")
    public String getAllMembers(Model model) {
        List<MemberListResponse> members = adminMemberService.getAllMembers();

        model.addAttribute("members", members);
        model.addAttribute("pageTitle", "회원 관리");
        model.addAttribute("isAllFilter", true);
        model.addAttribute("isMembers", true);
        return "admin/member-list";
    }

    // 활성 회원 목록
    @GetMapping("/active")
    public String getActiveMembers(Model model) {
        List<MemberListResponse> members = adminMemberService.getMembersByStatus(Status.ACTIVE);

        model.addAttribute("members", members);
        model.addAttribute("pageTitle", "활성 회원 목록");
        model.addAttribute("isActiveFilter", true);
        model.addAttribute("isMembers", true);
        return "admin/member-list";
    }

    // 정지 회원 목록
    @GetMapping("/suspended")
    public String getSuspendedMembers(Model model) {
        List<MemberListResponse> members = adminMemberService.getMembersByStatus(Status.SUSPENDED);

        model.addAttribute("members", members);
        model.addAttribute("pageTitle", "정지 회원 목록");
        model.addAttribute("isSuspendedFilter", true);
        model.addAttribute("isMembers", true);
        return "admin/member-list";
    }

    // 탈퇴 대기 회원 목록
    @GetMapping("/withdrawal")
    public String getWithdrawalMembers(Model model) {
        List<MemberListResponse> members = adminMemberService.getWithdrawalMembers();

        model.addAttribute("members", members);
        model.addAttribute("pageTitle", "탈퇴 대기 회원 목록");
        model.addAttribute("isWithdrawalFilter", true);
        model.addAttribute("isMembers", true);
        return "admin/member-list";
    }
}
