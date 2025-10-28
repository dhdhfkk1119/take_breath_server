package com.take.take_breath.community.community_post_like;

import lombok.Builder;
import lombok.Data;

public class CommunityPostLikeResponse {

    @Data
    @Builder
    public static class ResponseDTO {
        private Boolean liked;
        private Long likeCount;

        public ResponseDTO(Boolean liked, Long likeCount) {
            this.liked = liked;
            this.likeCount = likeCount;
        }
    }
}
