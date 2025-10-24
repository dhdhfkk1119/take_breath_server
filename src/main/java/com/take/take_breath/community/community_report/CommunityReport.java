package com.take.take_breath.community.community_report;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.community.community_comment.CommunityComment;
import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.community.community_report_process.CommunityReportProcess;
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
@Table(name = "community_report_tb")
public class CommunityReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO
    private Long reporterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @Column(nullable = false, length = 500)
    private String reason;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CommunityReportStatus status = CommunityReportStatus.PENDING;

    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityReportProcess> adminComments = new ArrayList<>();

    @CreationTimestamp
    private Timestamp createdAt;

    public String getTime() {
        return DateUtil.timestampFormat(createdAt);
    }

    public boolean isOwner(Long checkReporterId) {
        return this.reporterId != null && this.reporterId.equals(checkReporterId);
    }
}

