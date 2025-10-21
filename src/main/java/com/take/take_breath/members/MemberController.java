package com.take.take_breath.members;

import com.take.take_breath.members.dto.request.MemberLoginRequest;
import com.take.take_breath.members.dto.request.MemberSignupRequest;
import com.take.take_breath.members.dto.response.MemberTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody MemberSignupRequest req) {
        memberService.signup(req);
        return ResponseEntity.ok().build();
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody MemberLoginRequest req) {
        String token = memberService.login(req);
        MemberTokenResponse response = new MemberTokenResponse(token);
        return ResponseEntity.ok(response);
    }

}