package com.take.take_breath.admin.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view/community")
public class AdminCommunityController {

    /**
     * 카테고리 관리 페이지
     */
    @GetMapping("/categories")
    public String categoryView(Model model) {
        model.addAttribute("pageTitle", "커뮤니티 카테고리 관리");
        model.addAttribute("isCommunity", true);
        model.addAttribute("activeTab", "category");
        model.addAttribute("additionalCss", new String[]{"/css/admin-common.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin/community-category.js"});
        return "admin/community/category";
    }

    /**
     * 게시글 관리 페이지
     */
    @GetMapping("/posts")
    public String postsView(Model model) {
        model.addAttribute("pageTitle", "커뮤니티 게시글 관리");
        model.addAttribute("isCommunity", true);
        model.addAttribute("activeTab", "posts");
        model.addAttribute("additionalCss", new String[]{"/css/admin-common.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin/community-posts.js"});
        return "admin/community/posts";
    }

    /**
     * 댓글 관리 페이지
     */
    @GetMapping("/comments")
    public String commentsView(Model model) {
        model.addAttribute("pageTitle", "커뮤니티 댓글 관리");
        model.addAttribute("isCommunity", true);
        model.addAttribute("activeTab", "comments");
        model.addAttribute("additionalCss", new String[]{"/css/admin-common.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin/community-comments.js"});
        return "admin/community/comments";
    }
}