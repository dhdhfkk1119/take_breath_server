package com.take.take_breath.members.login;

import com.take.take_breath.members.login.dto.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/{provider}/login")
    public void login(@PathVariable String provider, HttpServletResponse response) throws IOException {
        String redirectUrl = loginService.getLoginPage(provider);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/{provider}/callback")
    public LoginResponse callback(
            @PathVariable String provider,
            @RequestParam String code,
            @RequestParam String state
    ) {
        return loginService.handleCallback(provider, code, state);
    }
}