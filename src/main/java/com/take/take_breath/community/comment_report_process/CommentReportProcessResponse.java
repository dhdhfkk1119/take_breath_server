package com.take.take_breath.community.comment_report_process;

import com.take.take_breath.community.comment_report.CommentReport;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import lombok.Builder;
import lombok.Data;

public class CommentReportProcessResponse {

    @Data
    public static class ProcessDTO {
        private Long processId;
        private Long reportId;
        private String commentContent;
        private Long adminId;
        private String adminName;
        private CommunityReportStatus status;
        private String adminComment;
        private String createdAt;

        @Builder
        public ProcessDTO(CommentReportProcess process) {
            this.processId = process.getId();
            this.reportId = process.getReport().getId();
            this.commentContent = process.getReport().getComment().getContent();
            this.adminId = process.getAdmin().getId();
            this.adminName = process.getAdmin().getName();
            this.status = process.getStatus();
            this.adminComment = process.getAdminComment();
            this.createdAt = process.getTime();
        }
    }

    @Data
    public static class ListDTO {
        private Long reportId;
        private Long commentId;
        private String commentContent;
        private Long reporterId;
        private String reporterName;
        private String reason;
        private CommunityReportStatus status;
        private String createdAt;
        private boolean isCommentDeleted;

        public ListDTO(CommentReport report) {
            this.reportId = report.getId();
            this.commentId = report.getComment().getId();
            this.commentContent = report.getComment().getContent();
            this.reporterId = report.getReporter().getId();
            this.reporterName = report.getReporter().getName();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();
        }

        public ListDTO(CommentReport report, String deletedCommentContent) {
            this.reportId = report.getId();
            this.commentId = report.getComment().getId();
            this.commentContent = deletedCommentContent;
            this.reporterId = report.getReporter().getId();
            this.reporterName = report.getReporter().getName();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();
            this.isCommentDeleted = true;
        }
    }

    @Data
    public static class DetailDTO {
        private Long reportId;
        private Long commentId;
        private String commentContent;
        private Long reporterId;
        private String reporterName;
        private String reason;
        private CommunityReportStatus status;
        private String createdAt;
        private boolean isCommentDeleted;

        @Builder
        public DetailDTO(CommentReport report, String deletedCommentContent) {
            this.reportId = report.getId();
            this.commentId = report.getComment().getId();
            this.commentContent = report.getComment().getContent();
            this.reporterId = report.getReporter().getId();
            this.reporterName = report.getReporter().getName();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();

            if (deletedCommentContent != null) {
                this.commentContent = deletedCommentContent;
                this.isCommentDeleted = true;
            } else {
                this.commentContent = report.getComment().getContent();
                this.isCommentDeleted = false;
            }
        }
    }
}