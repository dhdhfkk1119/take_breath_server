package com.take.take_breath.community.community_comment;

import com.take.take_breath._core._utils.DateUtil;
import lombok.Builder;
import lombok.Data;

public class CommunityCommentResponse {

    @Data
    public static class ResponseDTO {
        private Long id;
        private String content;
        private Long memberId;
        private String memberName;
        private Long postId;
        private String postTitle;
        private String createdAt;
        private String updatedAt;
        private boolean isModified;
        private boolean isDeleted;

        @Builder
        public ResponseDTO(CommunityComment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.memberId = comment.getMember() != null ? comment.getMember().getId() : null;
            this.memberName = comment.getMember() != null ? comment.getMember().getName() : null;
            this.postId = comment.getPost() != null ? comment.getPost().getId() : null;
            this.postTitle = comment.getPost() != null ? comment.getPost().getTitle() : "삭제된 게시글";
            this.createdAt = DateUtil.timestampFormat(comment.getCreatedAt());
            this.updatedAt = DateUtil.timestampFormat(comment.getUpdatedAt());
            this.isDeleted = comment.isDeleted();
            this.isModified = comment.isModified();
        }
    }
}