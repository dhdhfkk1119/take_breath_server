package com.take.take_breath.counselor;

import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath.counselor.dto.CounselorRequest;
import com.take.take_breath.counselor.dto.CounselorResponse;
import com.take.take_breath.members.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counselors")
@RequiredArgsConstructor
public class CounselorController {

    private final CounselorService counselorService;
    private final MemberService memberService;

    // ✅ 상담사 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid CounselorRequest req) {
        counselorService.signup(req);
        return ResponseEntity.ok("상담사 회원가입 요청이 완료되었습니다. 관리자 승인 후 이용 가능합니다.");
    }

    // ✅ 상담사 전체 조회 (좋아요 포함)
    @GetMapping
    public ResponseEntity<List<CounselorResponse>> getAllCounselors(HttpServletRequest request) {
        List<CounselorResponse> response = counselorService.getAllCounselors(request);
        return ResponseEntity.ok(response);
    }

    // ✅ 상담사 페이지네이션 (기존 그대로)
    @GetMapping("/page")
    public ResponseEntity<PageUtil.PageResponse<CounselorResponse>> getPagedCounselors(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        PageUtil.PageResponse<CounselorResponse> response = counselorService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    // ✅ 상담사 상세 조회 (좋아요 여부 포함)
    @GetMapping("/{id}")
    public ResponseEntity<CounselorResponse> getCounselorById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        CounselorResponse response = counselorService.findById(id, request);
        return ResponseEntity.ok(response);
    }

    // ✅ 해시태그 검색
    @GetMapping("/search")
    public ResponseEntity<List<CounselorResponse>> searchByHashtags(@RequestParam String keyword) {
        List<CounselorResponse> list = counselorService.searchByHashtags(keyword);
        return ResponseEntity.ok(list);
    }
}
