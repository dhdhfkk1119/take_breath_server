package com.take.take_breath.community.community_category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommunityCategoryRequest {

    @Data
    @NoArgsConstructor
    public static class SaveDTO {
        @NotBlank(message = "카테고리 이름은 필수입니다.")
        private String name;

        public SaveDTO(CommunityCategory category) {
            this.name = category.getName();
        }
    }

    @Data
    @NoArgsConstructor
    public static class UpdateDTO {
        @NotBlank(message = "카테고리 이름은 필수입니다.")
        private String name;

        public UpdateDTO(CommunityCategory category) {
            this.name = category.getName();
        }
    }
}