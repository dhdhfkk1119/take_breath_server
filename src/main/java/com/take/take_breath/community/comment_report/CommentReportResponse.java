package com.take.take_breath.community.comment_report;

import com.take.take_breath.community.comment_report_process.CommentReportProcess;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

public class CommentReportResponse {

    @Data
    public static class CreateDTO {
        private Long id;
        private String message;
        private Long commentId;
        private Long reporterId;
        private String reason;
        private CommunityReportStatus status;
        private String createdAt;

        @Builder
        public CreateDTO(CommentReport report, String message) {
            this.id = report.getId();
            this.message = message;
            this.commentId = report.getComment().getId();
            this.reporterId = report.getReporterId();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();
        }
    }

    @Data
    public static class ListDTO {
        private Long id;
        private Long commentId;
        private String reason;
        private CommunityReportStatus status;
        private String createdAt;

        public ListDTO(CommentReport report) {
            this.id = report.getId();
            this.commentId = report.getComment().getId();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private Long commentId;
        private String commentContent;
        private String reason;
        private CommunityReportStatus status;
        private String createdAt;
        private List<AdminCommentDTO> adminComments;

        public DetailDTO(CommentReport report) {
            this.id = report.getId();
            this.commentId = report.getComment().getId();
            this.commentContent = report.getComment().getContent();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();
            this.adminComments = report.getAdminComments().stream()
                    .map(process -> new AdminCommentDTO(process))
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class AdminCommentDTO {
        private Long id;
        private CommunityReportStatus status;
        private Long adminId;
        private String adminComment;
        private String createdAt;

        public AdminCommentDTO(CommentReportProcess process) {
            this.id = process.getId();
            this.status = process.getStatus();
            this.adminId = process.getAdminId();
            this.adminComment = process.getAdminComment();
            this.createdAt = process.getTime();
        }
    }
}