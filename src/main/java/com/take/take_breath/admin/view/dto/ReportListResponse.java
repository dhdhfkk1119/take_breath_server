package com.take.take_breath.admin.view.dto;

import com.take.take_breath.community.comment_report.CommentReport;
import com.take.take_breath.community.community_report.CommunityReport;
import com.take.take_breath.community.community_report.CommunityReportStatus;

import java.time.format.DateTimeFormatter;

public class ReportListResponse {
    private final Long id;
    private final String reportType;  // "POST" 또는 "COMMENT"
    private final String targetContent;
    private final String targetAuthorEmail;
    private final Long targetId;
    private final String reporterEmail;
    private final String reason;
    private final CommunityReportStatus status;
    private final String createdAt;

    // 타입 체크
    private final boolean isPostReport;
    private final boolean isCommentReport;

    // 상태 체크
    private final boolean isPending;
    private final boolean isApproved;
    private final boolean isRejected;

    // 게시글 신고 생성자
    public ReportListResponse(CommunityReport report) {
        this.id = report.getId();
        this.reportType = "POST";
        this.isPostReport = true;
        this.isCommentReport = false;

        if (report.getPost() != null) {
            this.targetContent = truncate(report.getPost().getTitle(), 30);
            this.targetAuthorEmail = report.getPost().getMember() != null
                    ? report.getPost().getMember().getEmail()
                    : "탈퇴회원";
            this.targetId = report.getPost().getId();
        } else {
            this.targetContent = "삭제된 게시글";
            this.targetAuthorEmail = "알 수 없음";
            this.targetId = null;
        }

        this.reporterEmail = report.getReporter() != null
                ? report.getReporter().getEmail()
                : "탈퇴회원";
        this.reason = report.getReason();
        this.status = report.getStatus();
        this.createdAt = report.getCreatedAt() != null
                ? report.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "";

        this.isPending = report.getStatus() == CommunityReportStatus.PENDING;
        this.isApproved = report.getStatus() == CommunityReportStatus.APPROVED;
        this.isRejected = report.getStatus() == CommunityReportStatus.REJECTED;
    }

    // 댓글 신고 생성자
    public ReportListResponse(CommentReport report) {
        this.id = report.getId();
        this.reportType = "COMMENT";
        this.isPostReport = false;
        this.isCommentReport = true;

        if (report.getComment() != null) {
            this.targetContent = truncate(report.getComment().getContent(), 30);
            this.targetAuthorEmail = report.getComment().getMember() != null
                    ? report.getComment().getMember().getEmail()
                    : "탈퇴회원";
            this.targetId = report.getComment().getId();
        } else {
            this.targetContent = "삭제된 댓글";
            this.targetAuthorEmail = "알 수 없음";
            this.targetId = null;
        }

        this.reporterEmail = report.getReporter() != null
                ? report.getReporter().getEmail()
                : "탈퇴회원";
        this.reason = report.getReason();
        this.status = report.getStatus();
        this.createdAt = report.getCreatedAt() != null
                ? report.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "";

        this.isPending = report.getStatus() == CommunityReportStatus.PENDING;
        this.isApproved = report.getStatus() == CommunityReportStatus.APPROVED;
        this.isRejected = report.getStatus() == CommunityReportStatus.REJECTED;
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public String getReportType() { return reportType; }
    public String getTargetContent() { return targetContent; }
    public String getTargetAuthorEmail() { return targetAuthorEmail; }
    public Long getTargetId() { return targetId; }
    public String getReporterEmail() { return reporterEmail; }
    public String getReason() { return reason; }
    public CommunityReportStatus getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    public boolean isPostReport() { return isPostReport; }
    public boolean isCommentReport() { return isCommentReport; }

    public boolean isPending() { return isPending; }
    public boolean isApproved() { return isApproved; }
    public boolean isRejected() { return isRejected; }
}