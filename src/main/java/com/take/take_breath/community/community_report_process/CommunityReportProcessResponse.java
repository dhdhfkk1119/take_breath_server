package com.take.take_breath.community.community_report_process;

import com.take.take_breath.community.community_report.CommunityReport;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import lombok.Builder;
import lombok.Data;

public class CommunityReportProcessResponse {

    @Data
    public static class ProcessDTO {
        private Long processId;
        private Long reportId;
        private String postTitle;
        private CommunityReportStatus status;
        private String adminComment;
        private String createdAt;

        @Builder
        public ProcessDTO(CommunityReportProcess process) {
            this.processId = process.getId();
            this.reportId = process.getReport().getId();
            this.postTitle = process.getReport().getPost().getTitle();
            this.status = process.getStatus();
            this.adminComment = process.getAdminComment();
            this.createdAt = process.getTime();
        }
    }

    @Data
    public static class ListDTO {
        private Long reportId;
        private Long postId;
        private String postTitle;
        private Long reporterId;
        private String reason;
        private CommunityReportStatus status;
        private String createdAt;
        private boolean isPostDeleted;

        public ListDTO(CommunityReport report) {
            this.reportId = report.getId();
            this.postId = report.getPost().getId();
            this.postTitle = report.getPost().getTitle();
            this.reporterId = report.getReporterId();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();
            this.isPostDeleted = report.getPost().isDeleted();
        }

        public ListDTO(CommunityReport report, String deletedPostTitle) {
            this.reportId = report.getId();
            this.postId = report.getPost().getId();
            this.postTitle = deletedPostTitle;
            this.reporterId = report.getReporterId();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();
            this.isPostDeleted = true;
        }
    }

    @Data
    public static class DetailDTO {
        private Long reportId;
        private Long postId;
        private String postTitle;
        private String postContent;
        private Long reporterId;
        private String reason;
        private CommunityReportStatus status;
        private String createdAt;
        private boolean isPostDeleted;

        @Builder
        public DetailDTO(CommunityReport report, String deletedPostTitle) {
            this.reportId = report.getId();
            this.postId = report.getPost().getId();
            this.postTitle = report.getPost().getTitle();
            this.postContent = report.getPost().getContent();
            this.reporterId = report.getReporterId();
            this.reason = report.getReason();
            this.status = report.getStatus();
            this.createdAt = report.getTime();

            if (deletedPostTitle != null) {
                this.postTitle = deletedPostTitle;
                this.postContent = "(삭제된 게시글)";
                this.isPostDeleted = true;
            } else {
                this.postTitle = report.getPost().getTitle();
                this.postContent = report.getPost().getContent();
                this.isPostDeleted = false;
            }
        }
    }
}