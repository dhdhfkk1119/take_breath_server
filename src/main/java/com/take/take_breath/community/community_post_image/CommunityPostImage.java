package com.take.take_breath.community.community_post_image;

import com.take.take_breath.community.community_post.CommunityPost;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import lombok.*;

@Entity
@Data
@ToString(exclude = "post")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "community_post_image_tb")
public class CommunityPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

}
