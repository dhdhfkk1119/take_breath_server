package com.take.take_breath.community.community_post;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@Slf4j
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    /**
     * 전체 게시글 목록 조회 (좋아요 여부 포함)
     * TODO: JWT 인증 구현 후 @Auth 추가
     */
    @GetMapping
    public ResponseEntity<ApiUtil.ApiResult<PageUtil.PageResponse<CommunityPostResponse.ListDTO>>> findAllPosts(
            @RequestParam(required = false) Long userId,  // TODO: JWT에서 추출로 변경
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long currentUserId = userId != null ? userId : 0L;

        Page<CommunityPostResponse.ListDTO> posts = communityPostService.findAllPosts(currentUserId, pageable);
        log.info("[전체 조회] userId={}, page={}, size={}", currentUserId, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(ApiUtil.success(PageUtil.PageResponse.of(posts)));
    }

    /**
     * 게시글 상세 조회 (좋아요 여부 포함, 조회수 증가)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityPostResponse.DetailDTO>> findPostDetail(
            @PathVariable Long id,
            @RequestParam(value = "commentSort", required = false, defaultValue = "latest") String commentSortType,
            @RequestParam(required = false) Long userId) {

        Long currentUserId = userId != null ? userId : 0L;

        CommunityPostResponse.DetailDTO post = communityPostService.findPostDetail(id, commentSortType, currentUserId);
        log.info("[상세 조회] postId={}, userId={}", id, currentUserId);
        return ResponseEntity.ok(ApiUtil.success(post));
    }

    /**
     * 게시글 작성
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.USER, Role.ADMIN}) 추가
     */
    @PostMapping
    public ResponseEntity<ApiUtil.ApiResult<CommunityPostResponse.ResponseDTO>> savePost(
            @Valid @RequestBody CommunityPostRequest.SaveDTO saveDTO,
            @RequestParam Long userId) {

        CommunityPostResponse.ResponseDTO savedPost = communityPostService.savePost(saveDTO, userId);
        log.info("[게시글 작성] postId={}, userId={}", savedPost.getId(), userId);
        return ResponseEntity.ok(ApiUtil.success(savedPost));
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityPostResponse.ResponseDTO>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody CommunityPostRequest.UpdateDTO updateDTO,
            @RequestParam Long userId) {

        CommunityPostResponse.ResponseDTO updatedPost = communityPostService.updatePost(id, updateDTO, userId);
        log.info("[게시글 수정] postId={}, userId={}", id, userId);
        return ResponseEntity.ok(ApiUtil.success(updatedPost));
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiUtil.ApiResult<String>> deletePost(
            @PathVariable Long id,
            @RequestParam Long userId) {

        communityPostService.deletePost(id, userId);
        log.info("[게시글 삭제] postId={}, userId={}", id, userId);
        return ResponseEntity.ok(ApiUtil.success("게시글이 삭제되었습니다."));
    }

    /**
     * 게시글 검색 (키워드, 카테고리, 정렬)
     * TODO: JWT 인증 구현 후 @Auth 추가
     */
    @GetMapping("/search")
    public ResponseEntity<ApiUtil.ApiResult<PageUtil.PageResponse<CommunityPostResponse.ListDTO>>> searchPosts(
            @ModelAttribute CommunityPostRequest.SearchDTO searchDTO,
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 10) Pageable pageable) {

        Long currentUserId = userId != null ? userId : 0L;

        Page<CommunityPostResponse.ListDTO> posts = communityPostService.searchPosts(
                currentUserId,
                searchDTO,
                pageable
        );

        log.info("[게시글 검색] keyword={}, categoryIds={}, sortType={}, userId={}",
                searchDTO.getKeyword(), searchDTO.getCategoryIds(), searchDTO.getSortType(), currentUserId);
        return ResponseEntity.ok(ApiUtil.success(PageUtil.PageResponse.of(posts)));
    }

    /**
     * 내가 작성한 게시글 목록 조회
     */
    @GetMapping("/mine")
    public ResponseEntity<ApiUtil.ApiResult<PageUtil.PageResponse<CommunityPostResponse.ListDTO>>> findMyPosts(
            @RequestParam Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CommunityPostResponse.ListDTO> posts = communityPostService.findPostsByUserId(
                userId,
                userId,
                pageable
        );
        log.info("[내 게시글 조회] userId={}", userId);
        return ResponseEntity.ok(ApiUtil.success(PageUtil.PageResponse.of(posts)));
    }

    /**
     * 특정 사용자의 게시글 목록 조회
     */
    @GetMapping("/user/{targetUserId}")
    public ResponseEntity<ApiUtil.ApiResult<PageUtil.PageResponse<CommunityPostResponse.ListDTO>>> findUserPosts(
            @PathVariable Long targetUserId,
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long currentUserId = userId != null ? userId : 0L;

        Page<CommunityPostResponse.ListDTO> posts = communityPostService.findPostsByUserId(
                targetUserId,
                currentUserId,
                pageable
        );
        log.info("[사용자 게시글 조회] targetUserId={}, currentUserId={}", targetUserId, currentUserId);
        return ResponseEntity.ok(ApiUtil.success(PageUtil.PageResponse.of(posts)));
    }

    /**
     * 관리자 전용: 게시글 강제 삭제
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.ADMIN}) 추가
     */
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<ApiUtil.ApiResult<String>> forceDeletePost(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam Long adminId) {

        communityPostService.forceDeletePost(id, reason, adminId);
        log.warn("[관리자 강제 삭제] adminId={}, postId={}, reason={}",adminId, id, reason);
        return ResponseEntity.ok(ApiUtil.success("게시글이 강제 삭제되었습니다."));
    }
}