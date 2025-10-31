package com.take.take_breath.admin.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping
    public String list(Model model) {
        var stats = adminMemberService.getMemberStats();
        model.addAttribute("stats", stats);
        model.addAttribute("pageTitle", "회원 관리");
        model.addAttribute("isMembers", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-dashboard.css"});

        return "admin/members";
    }
}