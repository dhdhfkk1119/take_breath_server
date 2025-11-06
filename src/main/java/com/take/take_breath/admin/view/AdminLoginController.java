package com.take.take_breath.admin.view;

import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Role;
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
    private final MemberRepository memberRepository; // ✅ 추가

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
            // 로그인 시도
            String token = adminAuthService.login(email, password);

            // 관리자 계정 확인 및 정보 조회
            Member admin = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다."));

            if (admin.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("관리자 계정이 아닙니다.");
            }

            // 세션에 관리자 정보 저장
            session.setAttribute("adminToken", token);
            session.setAttribute("adminEmail", email);
            session.setAttribute("adminId", admin.getId()); // ✅ 핵심 라인

            // 로그인 성공 시 대시보드로 이동
            return "redirect:/api/admin/view/dashboard";

        } catch (IllegalArgumentException e) {
            // 로그인 실패 시 다시 로그인 화면으로
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
