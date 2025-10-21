package com.take.take_breath.members.controller;

import com.take.take_breath.members.dto.MemberDTO;
import com.take.take_breath.members.service.MemberService;
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

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberDTO.SignupRequest req) {
        memberService.signup(req);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO.LoginRequest req) {
        String token = memberService.login(req);
        return ResponseEntity.ok(new MemberDTO.TokenResponse(token));
    }
}