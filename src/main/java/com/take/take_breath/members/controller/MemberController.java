package com.take.take_breath.members.controller;


import com.take.take_breath.members.Role;
import com.take.take_breath.members.service.MemberService;
import com.take.take_breath.members.dto.MemberRequest;
import com.take.take_breath.members.dto.MemberResponse;
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

    // 일반회원 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid MemberRequest req) {
        req.setRole(Role.USER);
        memberService.signup(req);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody MemberRequest req) {
        String token = memberService.login(req);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
    }

}