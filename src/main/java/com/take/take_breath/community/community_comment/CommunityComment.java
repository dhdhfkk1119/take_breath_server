package com.take.take_breath.community.community_comment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"post", "member"})
@Table(name = "community_comment_tb")
public class CommunityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonBackReference // 순환 참조 방지
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @Column(nullable = false)
    private Integer reportCount = 0;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public void update(String content) {
        this.content = content;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isModified() {
        return this.updatedAt != null && (this.updatedAt.getTime() - this.createdAt.getTime() > 10000);
    }

    public boolean isOwner(Long checkMemberId) {
        return this.member != null && this.member.getId().equals(checkMemberId);
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public void setCommunityPost(CommunityPost communityPost) {
        this.post = communityPost;
    }
}
