package com.take.take_breath.admin.view;

import com.take.take_breath.community.community_category.CommunityCategoryResponse;
import com.take.take_breath.community.community_comment.CommunityCommentResponse;
import com.take.take_breath.community.community_post.CommunityPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/community")
public class AdminCommunityRestController {

    private final AdminCommunityService adminCommunityService;

    // -----------------------------
    // 📘 카테고리 관리
    // -----------------------------
    @GetMapping("/categories")
    public List<CommunityCategoryResponse.ListDTO> getCategories() {
        return adminCommunityService.getAllCategories();
    }

    @PostMapping("/categories")
    public ResponseEntity<String> addCategory(@RequestBody CategoryAddRequest req) {
        adminCommunityService.addCategory(req.name());
        return ResponseEntity.ok("카테고리가 추가되었습니다.");
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        adminCommunityService.deleteCategory(id);
        return ResponseEntity.ok("카테고리가 삭제되었습니다.");
    }

    // -----------------------------
    // 📰 게시글 관리
    // -----------------------------
    @GetMapping("/posts")
    public List<CommunityPostResponse.ListDTO> getPosts() {
        return adminCommunityService.getAllPosts();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        adminCommunityService.deletePost(id);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    // -----------------------------
    // 💬 댓글 관리
    // -----------------------------
    @GetMapping("/comments")
    public List<CommunityCommentResponse.ResponseDTO> getComments() {
        return adminCommunityService.getAllComments();
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        adminCommunityService.deleteComment(id);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    // 🔹 요청 DTO
    public record CategoryAddRequest(String name) {}
}
