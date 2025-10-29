package com.take.take_breath.admin.view;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view")
public class AdminLoginController {

    private final AdminAuthService adminAuthService;

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        try {
            String token = adminAuthService.login(email, password);
            session.setAttribute("adminToken", token);
            session.setAttribute("adminEmail", email);
            return "redirect:/api/admin/view/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/login";
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/api/admin/view/login";
    }
}