package com.take.take_breath.community.community_post;

import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@Slf4j
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    /**
     * 게시글 목록 조회/검색 (키워드, 카테고리, 정렬)
     */
    @Auth(statuses = {Status.PENDING, Status.ACTIVE})
    @GetMapping
    public ResponseEntity<ApiUtil.ApiResult<PageUtil.PageResponse<CommunityPostResponse.ListDTO>>> searchPosts(
            @ModelAttribute CommunityPostRequest.SearchDTO searchDTO,
            HttpServletRequest request,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long memberId = (Long) request.getAttribute("memberId");
        Page<CommunityPostResponse.ListDTO> posts = communityPostService.searchPosts(memberId, searchDTO, pageable);

        log.info("[게시글 조회] keyword={}, categoryIds={}, sortType={}, memberId={}",
                searchDTO.getKeyword(), searchDTO.getCategoryIds(), searchDTO.getSortType(), memberId);
        return ResponseEntity.ok(ApiUtil.success(PageUtil.PageResponse.of(posts)));
    }

    /**
     * 게시글 상세 조회 (좋아요 여부 포함, 조회수 증가)
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/{id}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityPostResponse.DetailDTO>> findPostDetail(
            @PathVariable Long id,
            @RequestParam(value = "commentSort", required = false, defaultValue = "latest") String commentSortType,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        CommunityPostResponse.DetailDTO post = communityPostService.findPostDetail(id, commentSortType, memberId);

        log.info("[상세 조회] postId={}, memberId={}", id, memberId);
        return ResponseEntity.ok(ApiUtil.success(post));
    }

    /**
     * 게시글 작성
     */
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping
    public ResponseEntity<ApiUtil.ApiResult<CommunityPostResponse.ResponseDTO>> savePost(
            @Valid @RequestBody CommunityPostRequest.SaveDTO saveDTO,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        CommunityPostResponse.ResponseDTO savedPost = communityPostService.savePost(saveDTO, memberId);

        log.info("[게시글 작성] postId={}, memberId={}", savedPost.getId(), memberId);
        return ResponseEntity.ok(ApiUtil.success(savedPost));
    }

    /**
     * 게시글 수정
     */
    @Auth(statuses = {Status.ACTIVE})
    @PutMapping("/{id}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityPostResponse.ResponseDTO>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody CommunityPostRequest.UpdateDTO updateDTO,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        CommunityPostResponse.ResponseDTO updatedPost = communityPostService.updatePost(id, updateDTO, memberId);

        log.info("[게시글 수정] postId={}, memberId={}", id, memberId);
        return ResponseEntity.ok(ApiUtil.success(updatedPost));
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    @Auth(statuses = {Status.ACTIVE})
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiUtil.ApiResult<String>> deletePost(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        communityPostService.deletePost(id, memberId);

        log.info("[게시글 삭제] postId={}, memberId={}", id, memberId);
        return ResponseEntity.ok(ApiUtil.success("게시글이 삭제되었습니다."));
    }

    /**
     * 내가 작성한 게시글 목록 조회
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/mine")
    public ResponseEntity<ApiUtil.ApiResult<PageUtil.PageResponse<CommunityPostResponse.ListDTO>>> findMyPosts(
            HttpServletRequest request,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long memberId = (Long) request.getAttribute("memberId");
        Page<CommunityPostResponse.ListDTO> posts = communityPostService.findPostsByMemberId(memberId, memberId, pageable);

        log.info("[내 게시글 조회] memberId={}", memberId);
        return ResponseEntity.ok(ApiUtil.success(PageUtil.PageResponse.of(posts)));
    }

    /**
     * 특정 사용자의 게시글 목록 조회
     */
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/member/{targetMemberId}")
    public ResponseEntity<ApiUtil.ApiResult<PageUtil.PageResponse<CommunityPostResponse.ListDTO>>> findMemberPosts(
            @PathVariable Long targetMemberId,
            HttpServletRequest request,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long memberId = (Long) request.getAttribute("memberId");
        Page<CommunityPostResponse.ListDTO> posts = communityPostService.findPostsByMemberId(targetMemberId, memberId, pageable);


        log.info("[사용자 게시글 조회] targetMemberId={}, memberId={}", targetMemberId, memberId);
        return ResponseEntity.ok(ApiUtil.success(PageUtil.PageResponse.of(posts)));
    }

    /**
     * 관리자 전용: 게시글 강제 삭제
     */
    @Auth(roles = {Role.ADMIN}, statuses = {Status.ACTIVE})
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<ApiUtil.ApiResult<String>> forceDeletePost(
            @PathVariable Long id,
            @RequestParam String reason,
            HttpServletRequest request) {

        Long adminId = (Long) request.getAttribute("memberId");
        communityPostService.forceDeletePost(id, reason, adminId);

        log.warn("[관리자 강제 삭제] postId={}, reason={}, adminId={}", id, reason, adminId);
        return ResponseEntity.ok(ApiUtil.success("게시글이 강제 삭제되었습니다."));
    }
}