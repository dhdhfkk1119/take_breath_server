package com.take.take_breath.community.community_category;

import lombok.Data;

public class CommunityCategoryResponse {

    @Data
    public static class ListDTO {
        private Long id;
        private String name;

        public ListDTO(CommunityCategory category) {
            this.id = category.getId();
            this.name = category.getName();
        }
    }

    @Data
    public static class ResponseDTO {
        private Long id;
        private String name;

        public ResponseDTO(CommunityCategory category) {
            this.id = category.getId();
            this.name = category.getName();
        }
    }
}