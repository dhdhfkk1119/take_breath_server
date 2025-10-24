package com.take.take_breath.community.community_post_like;

import com.take.take_breath._core._utils.ApiUtil;
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
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.USER, Role.ADMIN}) 추가
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiUtil.ApiResult<CommunityPostLikeResponse.ResponseDTO>> toggleLike(
            @PathVariable Long postId,
            @RequestParam Long userId) {  // TODO: JWT에서 추출로 변경

        CommunityPostLikeResponse.ResponseDTO response = communityPostLikeService.toggleLike(postId, userId);
        log.info("[좋아요 토글] postId={}, userId={}, liked={}", postId, userId, response.getLiked());
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
     * TODO: JWT 인증 구현 후 @Auth 추가
     */
    @GetMapping("/{postId}/like/status")
    public ResponseEntity<ApiUtil.ApiResult<Boolean>> checkLikeStatus(
            @PathVariable Long postId,
            @RequestParam Long userId) {  // TODO: JWT에서 추출로 변경

        boolean liked = communityPostLikeService.isLiked(postId, userId);
        log.info("[좋아요 여부 확인] postId={}, userId={}, liked={}", postId, userId, liked);
        return ResponseEntity.ok(ApiUtil.success(liked));
    }
}