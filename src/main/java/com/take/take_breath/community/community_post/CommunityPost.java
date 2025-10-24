package com.take.take_breath.community.community_post;

import com.take.take_breath.community.community_category.CommunityCategory;
import com.take.take_breath.community.community_comment.CommunityComment;
import com.take.take_breath.community.community_post_image.CommunityPostImage;
import com.take.take_breath.community.community_post_like.CommunityPostLike;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "community_post_tb")
@Data
@ToString(exclude = {"images", "comments", "likes"})
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


    // TODO - 추후 연결 예정
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CommunityCategory category;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityPostImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityComment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public boolean isOwner(Long checkUserId) {
        return this.userId != null && this.userId.equals(checkUserId);
    }

    public boolean isModified() {
        return this.updatedAt != null && (this.updatedAt.getTime() - this.createdAt.getTime() > 10000);
    }

    public void addImage(CommunityPostImage image) {
        this.images.add(image);
    }

    public void addComment(CommunityComment comment) {
        this.comments.add(comment);
    }
}
