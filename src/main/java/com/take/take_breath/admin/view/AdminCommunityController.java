package com.take.take_breath.admin.view;

import com.take.take_breath.community.community_category.CommunityCategoryResponse;
import com.take.take_breath.community.community_post.CommunityPostResponse;
import com.take.take_breath.community.community_comment.CommunityCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view/community")
public class AdminCommunityController {

    private final AdminCommunityService adminCommunityService;

    /**
     * 카테고리 관리 페이지
     */
    @GetMapping("/categories")
    public String categoryView(Model model) {
        List<CommunityCategoryResponse.ListDTO> categories = adminCommunityService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "커뮤니티 카테고리 관리");
        model.addAttribute("isCommunity", true);
        model.addAttribute("isCategory", true);
        model.addAttribute("activeTab", "category");
        model.addAttribute("additionalCss", new String[]{"/css/admin-common.css"});
        model.addAttribute("additionalScript", new String[]{"/js/community-category.js"});
        return "admin/community-category";
    }

    // 카테고리 추가
    @PostMapping("/categories")
    public String addCategory(@RequestParam String name) {
        adminCommunityService.addCategory(name);
        return "redirect:/api/admin/view/community/categories";
    }

    // 카테고리 삭제
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        adminCommunityService.deleteCategory(id);
        return "redirect:/api/admin/view/community/categories";
    }


    /**
     * 게시글 관리 페이지
     */
    @GetMapping("/posts")
    public String postsView(Model model) {
        List<CommunityPostResponse.ListDTO> posts = adminCommunityService.getAllPosts();

        model.addAttribute("posts", posts);
        model.addAttribute("pageTitle", "커뮤니티 게시글 관리");
        model.addAttribute("isCommunity", true);
        model.addAttribute("isPost", true);
        model.addAttribute("activeTab", "posts");
        model.addAttribute("additionalCss", new String[]{"/css/admin-common.css"});
        model.addAttribute("additionalScript", new String[]{"/js/community-posts.js"});
        return "admin/community-posts";
    }

    // 게시글 강제 삭제
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        adminCommunityService.deletePost(id);
        return "redirect:/api/admin/view/community/posts";
    }

    /**
     * 댓글 관리 페이지
     */
    @GetMapping("/comments")
    public String commentsView(Model model) {
        List<CommunityCommentResponse.ResponseDTO> comments = adminCommunityService.getAllComments();

        model.addAttribute("comments", comments);
        model.addAttribute("pageTitle", "커뮤니티 댓글 관리");
        model.addAttribute("isCommunity", true);
        model.addAttribute("isComment", true);
        model.addAttribute("activeTab", "comments");
        model.addAttribute("additionalCss", new String[]{"/css/admin-common.css"});
        model.addAttribute("additionalScript", new String[]{"/js/community-comments.js"});
        return "admin/community-comments";
    }

    // 댓글 강제 삭제
    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        adminCommunityService.deleteComment(id);
        return "redirect:/api/admin/view/community/comments";
    }
}
