package com.take.take_breath.social.google;

import com.take.take_breath.members.MemberService;
import com.take.take_breath.members.dto.MemberResponseTo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/api/social")
@Slf4j
public class GoogleController {

    final MemberService memberService;

    // 컨트롤러 (POST /social-login)
    @PostMapping("/google-login")
    public ResponseEntity<?> socialLogin(@Valid @RequestBody SocialLoginRequest req) {
        MemberResponseTo.Login response = memberService.socialLogin(req);

        // 자체 JWT 토큰을 응답 헤더와 본문에 포함하여 반환
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.getAccessToken())
                .body(response);
    }
}

