package com.take.take_breath.community.community_comment;

import com.take.take_breath._core._utils.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/comments")
@RequiredArgsConstructor
@Slf4j
public class CommunityCommentController {

    private final CommunityCommentService communityCommentService;

    /**
     * 게시글의 댓글 목록 조회
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiUtil.ApiResult<List<CommunityCommentResponse.ResponseDTO>>> findCommentsByPostId(
            @PathVariable Long postId) {

        List<CommunityCommentResponse.ResponseDTO> comments = communityCommentService.findCommentsByPostId(postId);
        log.info("[댓글 목록 조회] postId={}, count={}", postId, comments.size());
        return ResponseEntity.ok(ApiUtil.success(comments));
    }

    /**
     * 댓글 작성
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.USER, Role.ADMIN}) 추가
     */
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityCommentResponse.ResponseDTO>> saveComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommunityCommentRequest.SaveDTO saveDTO,
            @RequestParam Long userId) {

        CommunityCommentResponse.ResponseDTO savedComment = communityCommentService.saveComment(postId, saveDTO, userId);
        log.info("[댓글 작성] commentId={}, postId={}, userId={}", savedComment.getId(), postId, userId);
        return ResponseEntity.ok(ApiUtil.success(savedComment));
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityCommentResponse.ResponseDTO>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommunityCommentRequest.UpdateDTO updateDTO,
            @RequestParam Long userId) {

        CommunityCommentResponse.ResponseDTO updatedComment = communityCommentService.updateComment(commentId, updateDTO, userId);
        log.info("[댓글 수정] commentId={}, userId={}", commentId, userId);
        return ResponseEntity.ok(ApiUtil.success(updatedComment));
    }

    /**
     * 댓글 삭제 (Soft Delete)
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiUtil.ApiResult<String>> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {

        communityCommentService.deleteComment(commentId, userId);
        log.info("[댓글 삭제] commentId={}, userId={}", commentId, userId);
        return ResponseEntity.ok(ApiUtil.success("댓글이 삭제되었습니다."));
    }

    /**
     * 관리자 전용: 댓글 강제 삭제
     */
    @DeleteMapping("/{commentId}/admin")
    public ResponseEntity<ApiUtil.ApiResult<String>> forceDeleteComment(
            @PathVariable Long commentId,
            @RequestParam String reason,
            @RequestParam Long adminId) {

        communityCommentService.forceDeleteComment(commentId, reason, adminId);
        log.warn("[관리자 댓글 강제 삭제] adminId={}, commentId={}, reason={}", adminId, commentId, reason);
        return ResponseEntity.ok(ApiUtil.success("댓글이 강제 삭제되었습니다."));
    }
}