package com.take.take_breath.community.comment_report;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.community.comment_report_process.CommentReportProcess;
import com.take.take_breath.community.community_comment.CommunityComment;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comment_report_tb")
public class CommentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO
    private Long reporterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommunityComment comment;

    @Column(nullable = false, length = 500)
    private String reason;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CommunityReportStatus status = CommunityReportStatus.PENDING;

    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentReportProcess> adminComments = new ArrayList<>();

    @CreationTimestamp
    private Timestamp createdAt;

    public String getTime() {
        return DateUtil.timestampFormat(createdAt);
    }

    public boolean isOwner(Long checkReporterId) {
        return this.reporterId != null && this.reporterId.equals(checkReporterId);
    }
}