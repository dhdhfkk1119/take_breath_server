package com.take.take_breath.community.community_report;

import com.take.take_breath.community.community_report_process.CommunityReportProcess;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

public class CommunityReportResponse {

    @Data
    public static class CreateDTO {
        private Long id;
        private String message;
        private Long postId;
        private Long reporterId;
        private String reason;
        private CommunityReportStatus status;
        private String createdAt;

        @Builder
        public CreateDTO(CommunityReport report, String message) {
            this.id = report.getId();
            this.message = message;
            this.postId = report.getPost().getId();
            this.reporterId = report.getReporterId();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();
        }
    }

    @Data
    public static class ListDTO {
        private Long id;
        private Long postId;
        private String reason;
        private CommunityReportStatus status;
        private String createdAt;

        public ListDTO(CommunityReport report) {
            this.id = report.getId();
            this.postId = report.getPost().getId();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private Long postId;
        private String postTitle;
        private String postContent;
        private String reason;
        private CommunityReportStatus status;
        private String createdAt;
        private List<AdminCommentDTO> adminComments;

        public DetailDTO(CommunityReport report) {
            this.id = report.getId();
            this.postId = report.getPost().getId();
            this.postTitle = report.getPost().getTitle();
            this.postContent = report.getPost().getContent();
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

        public AdminCommentDTO(CommunityReportProcess process) {
            this.id = process.getId();
            this.status = process.getStatus();
            this.adminId = process.getAdminId();
            this.adminComment = process.getAdminComment();
            this.createdAt = process.getTime();
        }
    }
}






