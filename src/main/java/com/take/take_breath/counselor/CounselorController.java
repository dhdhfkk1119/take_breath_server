package com.take.take_breath.counselor;

import com.take.take_breath.counselor.dto.CounselorRequest;
import com.take.take_breath.counselor.dto.CounselorResponse;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberService;
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
        counselorService.signup(req);
        return ResponseEntity.ok("상담사 회원가입 요청이 완료되었습니다. 관리자 승인 후 이용 가능합니다.");
    }

    // 상담사 전체 조회
    @GetMapping
    public ResponseEntity<?> getAllCounselors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ResponseEntity.ok(counselorService.findAll(page, size, sortBy, direction));
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
