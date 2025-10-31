package com.take.take_breath.terms;

import com.take.take_breath.terms.dto.MemberTermResponse;
import com.take.take_breath.terms.dto.MemberTermsRequest;
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



    @GetMapping()
    public ResponseEntity<?> getListTerms() {
        List<MemberTermResponse> response = termsService.termList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTerms(@PathVariable("id")Long id) {
        MemberTermResponse response = termsService.getTerm(id);
        return ResponseEntity.ok(response);
    }
}
