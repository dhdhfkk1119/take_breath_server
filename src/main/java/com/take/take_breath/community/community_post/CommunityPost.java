package com.take.take_breath.community.community_post;

import com.take.take_breath.community.community_category.CommunityCategory;
import com.take.take_breath.community.community_comment.CommunityComment;
import com.take.take_breath.community.community_post_image.CommunityPostImage;
import com.take.take_breath.community.community_post_like.CommunityPostLike;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "community_post_tb")
@Getter
@Setter
@ToString(exclude = {"member", "category", "images", "comments", "likes"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 512)
    private String thumbnailImageUrl;

    @Builder.Default
    @Column(nullable = false)
    private int likeCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private int viewCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer reportCount = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CommunityCategory category;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @BatchSize(size = 100)
    private List<CommunityPostImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<CommunityComment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<CommunityPostLike> likes = new ArrayList<>();

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isOwner(Long checkMemberId) {
        return this.member != null && this.member.getId().equals(checkMemberId);
    }

    public boolean isModified() {
        return this.updatedAt != null && (this.updatedAt.getTime() - this.createdAt.getTime() > 10000);
    }

    public void addImage(CommunityPostImage image) {
        if (image.getPost() != this) {
            image.setPost(this);
        }
        this.images.add(image);
    }

    public void addComment(CommunityComment comment) {
        if (comment.getPost() != this) {
            comment.setPost(this);
        }
        this.comments.add(comment);
    }
}
