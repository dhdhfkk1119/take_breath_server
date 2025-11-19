package com.take.take_breath.social;

import com.take.take_breath.members.login.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class SocialLoginController {

    private final SocialLoginService socialLoginService;


    @PostMapping("/naver-login")
    public ResponseEntity<LoginResponse> socialLogin(
            @RequestBody SocialLoginRequest request
    ) {
        log.info("소셜 로그인 API : {} : {} " , request.getIdToken(),request.getProvider());
        return ResponseEntity.ok(socialLoginService.loginOrSignup(request));
    }
}
