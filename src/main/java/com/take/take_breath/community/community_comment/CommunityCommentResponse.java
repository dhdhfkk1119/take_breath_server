package com.take.take_breath.community.community_comment;

import com.take.take_breath._core._utils.DateUtil;
import lombok.Builder;
import lombok.Data;

public class CommunityCommentResponse {

    @Data
    public static class ResponseDTO {
        private Long id;
        private String content;
        private Long userId;  // TODO - User 연동 후 userName으로 변경
        private String createdAt;
        private String updatedAt;
        private boolean isModified;
        private boolean isDeleted;

        @Builder
        public ResponseDTO(CommunityComment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.userId = comment.getUserId();
            this.createdAt = DateUtil.timestampFormat(comment.getCreatedAt());
            this.updatedAt = DateUtil.timestampFormat(comment.getUpdatedAt());
            this.isDeleted = comment.isDeleted();
            this.isModified = comment.isModified();
        }
    }
}