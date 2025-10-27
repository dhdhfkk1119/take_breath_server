package com.take.take_breath.community.community_post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class CommunityPostRequest {

    @Data
    @NoArgsConstructor
    public static class SaveDTO {
        @NotBlank(message = "제목은 필수입니다.")
        private String title;
        @NotBlank(message = "내용은 필수입니다.")
        private String content;
        private Long categoryId;
        private List<String> imageUrls;

        public SaveDTO(CommunityPost post) {
            this.title = post.getTitle();
            this.content = post.getContent();
            this.categoryId = post.getCategory() != null ? post.getCategory().getId() : null;
            this.imageUrls = post.getImages().stream()
                    .map(img -> img.getImageUrl())
                    .toList();
        }
    }

    @Data
    @NoArgsConstructor
    public static class UpdateDTO {
        @NotBlank(message = "제목은 필수입니다.")
        private String title;
        @NotBlank(message = "내용은 필수입니다.")
        private String content;
        private Long categoryId;
        private List<String> addImageUrls;
        private List<Long> deleteImageIds;

        public UpdateDTO(CommunityPost post) {
            this.title = post.getTitle();
            this.content = post.getContent();
            this.categoryId = post.getCategory() != null ? post.getCategory().getId() : null;
        }
    }

    @Data
    public static class SearchDTO {
        private String keyword;
        private List<Long> categoryIds;
        private CommunityPostSortType sortType;
    }
}
