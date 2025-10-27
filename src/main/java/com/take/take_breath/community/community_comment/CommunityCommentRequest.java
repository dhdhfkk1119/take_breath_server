package com.take.take_breath.community.community_comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommunityCommentRequest {

    @Data
    @NoArgsConstructor
    public static class SaveDTO {
        @NotBlank(message = "댓글 내용은 필수입니다.")
        private String content;

        public SaveDTO(CommunityComment comment) {
            this.content = comment.getContent();
        }
    }

    @Data
    @NoArgsConstructor
    public static class UpdateDTO {
        @NotBlank(message = "댓글 내용은 필수입니다.")
        private String content;

        public UpdateDTO(CommunityComment comment) {
            this.content = comment.getContent();
        }
    }
}
