package com.take.take_breath.community.community_report_process;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.community.community_report.CommunityReport;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "community_report_process_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"report", "admin"})
public class CommunityReportProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    @JsonBackReference
    private CommunityReport report;

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