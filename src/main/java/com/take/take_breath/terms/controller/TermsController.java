package com.take.take_breath.terms.controller;

import com.take.take_breath.terms.dto.MemberTermsRequest;
import com.take.take_breath.terms.service.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermsController {
    private final TermsService termsService;

    @PostMapping("/{memberId}/agree")
    public ResponseEntity<?> agreeToTerms(
            @PathVariable Long memberId,
            @RequestBody List<MemberTermsRequest> requests) {
        termsService.saveMemberAgreements(memberId, requests);
        return ResponseEntity.ok("약관 동의가 저장되었습니다.");
    }
}
