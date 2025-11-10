package com.take.take_breath.counselor.like;

import com.take.take_breath.counselor.dto.CounselorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

    // 내가 좋아요 누른 상담사 목록
    @GetMapping("/my")
    public ResponseEntity<List<CounselorResponse>> myLikedCounselors(@RequestParam Long memberId) {
        var likes = likeService.getLikedCounselors(memberId)
                .stream()
                .map(like -> {
                    var counselor = like.getCounselor();
                    var dto = CounselorResponse.from(counselor);
                    dto.setLikeCount(likeService.countLikes(counselor.getId())); // 총 좋아요 수
                    dto.setLikedByMe(true); // 내가 좋아요한 목록이므로 true 고정
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(likes);
    }

    // 특정 상담사의 좋아요 개수 조회
    @GetMapping("/{counselorId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long counselorId) {
        return ResponseEntity.ok(likeService.countLikes(counselorId));
    }
}
