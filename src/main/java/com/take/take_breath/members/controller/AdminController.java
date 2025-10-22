package com.take.take_breath.members.controller;

import com.take.take_breath.members.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;

    @PostMapping("/approve/{memberId}")
    public ResponseEntity<?> approveCounselor(@PathVariable Long memberId) {
        memberService.approveCounselor(memberId);
        return ResponseEntity.ok("상담사 승인 완료");
    }
}
