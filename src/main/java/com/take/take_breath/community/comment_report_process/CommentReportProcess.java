package com.take.take_breath.community.comment_report_process;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.community.comment_report.CommentReport;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "comment_report_process_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReportProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    @JsonBackReference
    private CommentReport report;

    @Enumerated(EnumType.STRING)
    private CommunityReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Member admin;

    @Column(length = 500)
    private String adminComment;

    @CreationTimestamp
    private Timestamp createdAt;

    public String getTime() {
        return DateUtil.timestampFormat(createdAt);
    }
}