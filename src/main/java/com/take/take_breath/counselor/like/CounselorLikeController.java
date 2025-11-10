package com.take.take_breath.counselor.like;

import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.counselor.dto.CounselorResponse;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/counselors/likes")
@RequiredArgsConstructor
public class CounselorLikeController {

    private final CounselorLikeService likeService;

    // 좋아요 토글
    // 좋아요 토글 엔드포인트 (CounselorController.java)

    // 💡 1단계: @Auth 어노테이션이 올바르게 적용되었는지 확인
    @Auth(statuses = {Status.ACTIVE}) // 예시: 토큰 검증 및 memberId 추출을 담당하는 어노테이션
    @PostMapping("/{counselorId}")
    public ResponseEntity<String> toggleLike(
            @PathVariable Long counselorId,
            HttpServletRequest request) {

        // 💡 2단계: 추출된 memberId의 null 여부를 즉시 확인
        Long memberId = (Long) request.getAttribute("memberId");

        if (memberId == null) {
            // 인증 필터에서 처리되지 못하고 여기까지 왔다면 여기서 401 UNAUTHORIZED 응답
            // 이 오류는 500이 아니라 401로 응답해야 합니다.
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "토큰이 없거나 유효하지 않습니다. (Member ID 누락)"
            );
        }

        // 3단계: 서비스 로직 호출
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
