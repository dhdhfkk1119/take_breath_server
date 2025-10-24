package com.take.take_breath.community.community_report_process;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.community.community_report.CommunityReport;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "community_report_process_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityReportProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    @JsonBackReference // 순환 참조 방지
    private CommunityReport report;

    @Enumerated(EnumType.STRING)
    private CommunityReportStatus status;

    // TODO
    private Long adminId;

    @Column(length = 500)
    private String adminComment;

    @CreationTimestamp
    private Timestamp createdAt;

    public String getTime(){
        return DateUtil.timestampFormat(createdAt);
    }
}