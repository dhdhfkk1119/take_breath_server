package com.take.take_breath.community.community_post_like;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@Slf4j
public class CommunityPostLikeController {

    private final CommunityPostLikeService communityPostLikeService;

    /**
     * 좋아요 토글 (좋아요/취소)
     */
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiUtil.ApiResult<CommunityPostLikeResponse.ResponseDTO>> toggleLike(
            @PathVariable Long postId,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        CommunityPostLikeResponse.ResponseDTO response = communityPostLikeService.toggleLike(postId, memberId);
        log.info("[좋아요 토글] postId={}, memberId={}, liked={}", postId, memberId, response.getLiked());
        return ResponseEntity.ok(ApiUtil.success(response));
    }

    /**
     * 게시글 좋아요 수 조회
     */
    @GetMapping("/{postId}/like/count")
    public ResponseEntity<ApiUtil.ApiResult<Long>> getLikeCount(@PathVariable Long postId) {
        Long count = communityPostLikeService.getLikeCount(postId);
        log.info("[좋아요 수 조회] postId={}, count={}", postId, count);
        return ResponseEntity.ok(ApiUtil.success(count));
    }

    /**
     * 좋아요 여부 확인
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/{postId}/like/status")
    public ResponseEntity<ApiUtil.ApiResult<Boolean>> checkLikeStatus(
            @PathVariable Long postId,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        boolean liked = communityPostLikeService.isLiked(postId, memberId);
        log.info("[좋아요 여부 확인] postId={}, memberId={}, liked={}", postId, memberId, liked);
        return ResponseEntity.ok(ApiUtil.success(liked));
    }
}