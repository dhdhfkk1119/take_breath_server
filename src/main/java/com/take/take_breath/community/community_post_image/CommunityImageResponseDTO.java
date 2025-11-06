package com.take.take_breath.community.community_post_image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommunityImageResponseDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageDTO {
        private Long id;
        private String imageUrl;
    }
}
