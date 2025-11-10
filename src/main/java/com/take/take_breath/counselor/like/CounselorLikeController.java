package com.take.take_breath.counselor.like;

import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath.counselor.dto.CounselorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counselors/likes")
@RequiredArgsConstructor
public class CounselorLikeController {

    private final CounselorLikeService likeService;

    // 좋아요 토글
    @PostMapping("/{counselorId}")
    public ResponseEntity<String> toggleLike(
            @PathVariable Long counselorId,
            HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        boolean liked = likeService.toggleLike(counselorId, memberId);
        String message = liked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.";
        return ResponseEntity.ok(message);
    }

    // 내가 좋아요 누른 상담사 목록 - (페이지네이션)
    @GetMapping("/my")
    public ResponseEntity<PageUtil.PageResponse<CounselorResponse>> myLikedCounselors(
            @RequestParam Long memberId,
            Pageable pageable) {
        PageUtil.PageResponse<CounselorResponse> response = likeService.getLikedCounselors(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    // 특정 상담사의 좋아요 개수 조회
    @GetMapping("/{counselorId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long counselorId) {
        return ResponseEntity.ok(likeService.countLikes(counselorId));
    }
}
