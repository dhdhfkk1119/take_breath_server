package com.take.take_breath.community.community_comment;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
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
     */
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityCommentResponse.ResponseDTO>> saveComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommunityCommentRequest.SaveDTO saveDTO,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        CommunityCommentResponse.ResponseDTO savedComment = communityCommentService.saveComment(postId, saveDTO, memberId);
        log.info("[댓글 작성] commentId={}, postId={}, memberId={}", savedComment.getId(), postId, memberId);
        return ResponseEntity.ok(ApiUtil.success(savedComment));
    }

    /**
     * 댓글 수정
     */
    @Auth(statuses = {Status.ACTIVE})
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityCommentResponse.ResponseDTO>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommunityCommentRequest.UpdateDTO updateDTO,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        CommunityCommentResponse.ResponseDTO updatedComment = communityCommentService.updateComment(commentId, updateDTO, memberId);
        log.info("[댓글 수정] commentId={}, memberId={}", commentId, memberId);
        return ResponseEntity.ok(ApiUtil.success(updatedComment));
    }

    /**
     * 댓글 삭제 (Soft Delete)
     */
    @Auth(statuses = {Status.ACTIVE})
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiUtil.ApiResult<String>> deleteComment(
            @PathVariable Long commentId,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        communityCommentService.deleteComment(commentId, memberId);
        log.info("[댓글 삭제] commentId={}, memberId={}", commentId, memberId);
        return ResponseEntity.ok(ApiUtil.success("댓글이 삭제되었습니다."));
    }

    /**
     * 관리자 전용: 댓글 강제 삭제
     */
    @Auth(statuses = {Status.ACTIVE})
    @DeleteMapping("/{commentId}/admin")
    public ResponseEntity<ApiUtil.ApiResult<String>> forceDeleteComment(
            @PathVariable Long commentId,
            @RequestParam String reason,
            HttpServletRequest request) {

        Long adminId = (Long) request.getAttribute("memberId");
        communityCommentService.forceDeleteComment(commentId, reason, adminId);
        log.warn("[관리자 댓글 강제 삭제] adminId={}, commentId={}, reason={}", adminId, commentId, reason);
        return ResponseEntity.ok(ApiUtil.success("댓글이 강제 삭제되었습니다."));
    }
}