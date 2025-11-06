package com.take.take_breath.admin.view;

import com.take.take_breath.community.community_category.CommunityCategoryRequest;
import com.take.take_breath.community.community_category.CommunityCategoryResponse;
import com.take.take_breath.community.community_category.CommunityCategoryService;
import com.take.take_breath.community.community_comment.CommunityComment;
import com.take.take_breath.community.community_comment.CommunityCommentResponse;
import com.take.take_breath.community.community_comment.CommunityCommentService;
import com.take.take_breath.community.community_comment.CommunityCommentRepository;
import com.take.take_breath.community.community_post.CommunityPostRequest;
import com.take.take_breath.community.community_post.CommunityPostResponse;
import com.take.take_breath.community.community_post.CommunityPostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관리자 전용 커뮤니티 관리 서비스
 * - 기존 커뮤니티 서비스 로직을 호출해서 SSR에서 사용
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommunityService {

    private final CommunityCategoryService categoryService;
    private final CommunityPostService postService;
    private final CommunityCommentService commentService;
    private final CommunityCommentRepository commentRepository;
    private final HttpSession session;

    // 카테고리 관리
    public List<CommunityCategoryResponse.ListDTO> getAllCategories() {
        return categoryService.findAllCategories();
    }

    @Transactional
    public void addCategory(String name) {
        Long adminId = getAdminId();
        CommunityCategoryRequest.SaveDTO dto = new CommunityCategoryRequest.SaveDTO();
        dto.setName(name);
        categoryService.saveCategory(dto, adminId);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Long adminId = getAdminId();
        categoryService.deleteCategory(id, adminId);
    }

    // 게시글 관리
    public List<CommunityPostResponse.ListDTO> getAllPosts() {
        Long adminId = getAdminId();
        CommunityPostRequest.SearchDTO searchDTO = new CommunityPostRequest.SearchDTO();
        return postService.searchPosts(adminId, searchDTO, PageRequest.of(0, 100))
                .getContent();
    }

    @Transactional
    public void deletePost(Long id) {
        Long adminId = getAdminId();
        postService.forceDeletePost(id, "관리자 페이지에서 강제 삭제됨", adminId);
    }

    // 댓글 관리
    public List<CommunityCommentResponse.ResponseDTO> getAllComments() {
        return commentRepository.findAll().stream()
                .filter(c -> !c.isDeleted())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(100)
                .map(c -> new CommunityCommentResponse.ResponseDTO(c))
                .toList();
    }

    @Transactional
    public void deleteComment(Long id) {
        Long adminId = getAdminId();
        commentService.forceDeleteComment(id, "관리자 페이지에서 강제 삭제됨", adminId);
    }


    // 세션에서 관리자 ID 추출
    private Long getAdminId() {
        Object adminIdObj = session.getAttribute("adminId");
        if (adminIdObj == null) {
            throw new IllegalStateException("관리자 세션이 만료되었거나 로그인되지 않았습니다.");
        }
        return (Long) adminIdObj;
    }
}
