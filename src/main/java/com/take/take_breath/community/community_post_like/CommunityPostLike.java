package com.take.take_breath.community.community_post_like;

import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString(exclude = "post")
@Entity
@Table(name = "community_post_like_tb",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "member_id"})})
public class CommunityPostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
