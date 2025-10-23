package com.take.take_breath.counselor.controller;

import com.take.take_breath.counselor.dto.CounselorRequest;
import com.take.take_breath.counselor.dto.CounselorResponse;
import com.take.take_breath.counselor.service.CounselorService;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.dto.MemberRequest;
import com.take.take_breath.members.entity.Member;
import com.take.take_breath.members.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counselors")
@RequiredArgsConstructor
public class CounselorController {

    private final CounselorService counselorService;
    private final MemberService memberService;

    // 상담사 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid CounselorRequest req) {

        MemberRequest memberReq = MemberRequest.builder()
                .email(req.getEmail())
                .password(req.getPassword())
                .name(req.getName())
                .phone(req.getPhone())
                .address(req.getAddress())
                .termsService(req.isTermsService())
                .termsPrivacy(req.isTermsPrivacy())
                .termsThirdParty(req.isTermsThirdParty())
                .termsMarketing(req.isTermsMarketing())
                .role(Role.COUNSELOR)
                .build();

        Member member = memberService.signup(memberReq);

        counselorService.signup(member.getId(), req);

        return ResponseEntity.ok("상담사 회원가입 요청이 완료되었습니다. 관리자 승인 후 이용 가능합니다.");
    }

    // 상담사 전체 조회
    @GetMapping
    public ResponseEntity<List<CounselorResponse>> getAllCounselors() {
        return ResponseEntity.ok(counselorService.findAll());
    }

    // 상담사 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<CounselorResponse> getCounselorById(@PathVariable Long id) {
        CounselorResponse response = counselorService.findById(id);
        return ResponseEntity.ok(response);
    }

    // 해시태그 검색
    @GetMapping("/search")
    public ResponseEntity<List<CounselorResponse>> searchByHashtags(@RequestParam String keyword) {
        List<CounselorResponse> list = counselorService.searchByHashtags(keyword);
        return ResponseEntity.ok(list);
    }
}
