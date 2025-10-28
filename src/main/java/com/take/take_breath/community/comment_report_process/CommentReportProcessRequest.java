package com.take.take_breath.community.comment_report_process;

import com.take.take_breath.community.community_report.CommunityReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommentReportProcessRequest {

    @Data
    @NoArgsConstructor
    public static class UpdateStatusDTO {
        @NotNull(message = "신고 처리 상태는 필수입니다.")
        private CommunityReportStatus status;

        private String adminComment;

        public UpdateStatusDTO(CommentReportProcess process) {
            this.status = process.getStatus();
            this.adminComment = process.getAdminComment();
        }
    }
}