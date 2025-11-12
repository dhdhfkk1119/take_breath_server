package com.take.take_breath.counselor.like;

import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.counselor.dto.CounselorResponse;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/counselors/likes")
@RequiredArgsConstructor
public class CounselorLikeController {

    private final CounselorLikeService likeService;

    @Auth(statuses = {Status.ACTIVE}) // 예시: 토큰 검증 및 memberId 추출을 담당하는 어노테이션
    @PostMapping("/{counselorId}")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long counselorId,
            HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        if (memberId == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "토큰이 없거나 유효하지 않습니다. (Member ID 누락)"
            );
        }

        // 3단계: 서비스 로직 호출
        boolean liked = likeService.toggleLike(counselorId, memberId);
        return ResponseEntity.ok(liked);
    }

    // 내가 좋아요한 상담사 목록 (페이지네이션)
    @GetMapping("/my")
    public ResponseEntity<PageUtil.PageResponse<CounselorResponse>> myLikedCounselors(
            Pageable pageable,
            HttpServletRequest request
    ) {
        PageUtil.PageResponse<CounselorResponse> response = likeService.getLikedCounselors(pageable, request);
        return ResponseEntity.ok(response);
    }

    // 특정 상담사 좋아요 개수
    @GetMapping("/{counselorId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long counselorId) {
        return ResponseEntity.ok(likeService.countLikes(counselorId));
    }
}
