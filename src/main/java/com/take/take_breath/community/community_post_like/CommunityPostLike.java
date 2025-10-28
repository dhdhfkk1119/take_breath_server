package com.take.take_breath.community.community_post_like;

import com.take.take_breath.community.community_post.CommunityPost;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString(exclude = "post")
@Entity
@Table(name = "community_post_like_tb",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "user_id"})})
public class CommunityPostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private CommunityPost post;

    // TODO - 추후 연결 예정
    @Column(name = "user_id")
    private Long userId;
}
