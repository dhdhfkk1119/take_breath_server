package com.take.take_breath.social.google;

import com.take.take_breath.members.MemberService;
import com.take.take_breath.members.dto.MemberResponseTo;
import com.take.take_breath.social.SocialLoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/social")
@Slf4j
public class GoogleController {

    final MemberService memberService;


    @PostMapping("/google-login")
    public ResponseEntity<?> socialLogin(@Valid @RequestBody SocialLoginRequest req) {
        log.info("========== socialLogin 컨트롤러 진입 ==========");
        log.info("Received provider: {}", req.getProvider());
        log.info("Received idToken: {}", req.getIdToken() != null ? req.getIdToken().substring(0, 50) + "..." : "null");

        try {
            MemberResponseTo.Login response = memberService.socialLogin(req);
            log.info("서비스 응답 성공");

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + response.getAccessToken())
                    .body(response);
        } catch (Exception e) {
            log.error("socialLogin 중 오류 발생", e);
            throw e;
        }
    }
}

