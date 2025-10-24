package com.take.take_breath.community.community_post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.community.community_comment.CommunityComment;
import com.take.take_breath.community.community_comment.CommunityCommentResponse;
import lombok.Builder;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommunityPostResponse {

    // 전체 조회용 DTO
    @Data
    public static class ListDTO {
        private Long id;
        private String title;
        private Long categoryId;
        private String categoryName;
        private String preview;
        private String thumbnail;
        private int likeCount;
        private int viewCount;
        private int commentCount;
        private String createdAt;
        private boolean isModified;
        private boolean liked;

        @Builder
        public ListDTO(CommunityPost post, boolean liked) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.categoryId = post.getCategory() != null ? post.getCategory().getId() : null;
            this.categoryName = post.getCategory() != null ? post.getCategory().getName() : null;
            this.likeCount = post.getLikeCount();
            this.viewCount = post.getViewCount();
            this.commentCount = post.getComments() != null ? post.getComments().size() : 0;
            this.createdAt = DateUtil.timestampFormat(post.getCreatedAt());
            this.isModified = post.isModified();
            this.liked = liked;

            // 썸네일: 첫 번째 이미지만
            this.thumbnail = post.getImages() != null && !post.getImages().isEmpty()
                    ? post.getImages().get(0).getImageUrl()
                    : null;

            // 내용 미리보기 (50자)
            String content = post.getContent();
            this.preview = content != null && content.length() > 50
                    ? content.substring(0, 50) + "..."
                    : content;
        }
    }

    // 상세 조회용 DTO
    @Data
    public static class DetailDTO {
        private Long id;
        private String title;
        private String content;
        private Long userId;
        private String categoryName;
        private Long categoryId;
        private int likeCount;
        private int viewCount;
        private int commentCount;
        private String createdAt;
        private String updatedAt;
        private boolean isModified;
        private boolean liked;
        private List<String> imageUrls;
        private List<CommunityCommentResponse.ResponseDTO> comments;

        @Builder
        public DetailDTO(CommunityPost post, String commentSortType, boolean liked) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.userId = post.getUserId();
            this.categoryName = post.getCategory() != null ? post.getCategory().getName() : null;
            this.categoryId = post.getCategory() != null ? post.getCategory().getId() : null;
            this.likeCount = post.getLikeCount();
            this.viewCount = post.getViewCount();
            this.commentCount = post.getComments() != null ? post.getComments().size() : 0;
            this.createdAt = DateUtil.timestampFormat(post.getCreatedAt());
            this.updatedAt = DateUtil.timestampFormat(post.getUpdatedAt());
            this.isModified = post.isModified();
            this.liked = liked;

            // 이미지 URL 리스트
            this.imageUrls = post.getImages() != null
                    ? post.getImages().stream()
                    .map(image -> image.getImageUrl())
                    .collect(Collectors.toList())
                    : List.of();

            // 댓글 정렬
            if (post.getComments() != null) {
                this.comments = post.getComments().stream()
                        .filter(comment -> !comment.isDeleted())
                        .sorted("latest".equalsIgnoreCase(commentSortType)
                                ? Comparator.comparing((CommunityComment comment) -> comment.getCreatedAt()).reversed()
                                : Comparator.comparing((CommunityComment comment) -> comment.getCreatedAt()))
                        .map(comment -> new CommunityCommentResponse.ResponseDTO(comment))
                        .collect(Collectors.toList());
            } else {
                this.comments = List.of();
            }
        }
    }

    // 작성/수정 응답 DTO
    @Data
    public static class ResponseDTO {
        private Long id;
        private String title;
        private String content;
        private Long categoryId;

        @Builder
        public ResponseDTO(CommunityPost post) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.categoryId = post.getCategory().getId();
        }
    }
}
