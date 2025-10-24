package com.take.take_breath.community.community_post_like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, Long> {

    // 여러 게시글의 좋아요 여부 확인
    @Query("SELECT cpl.post.id FROM CommunityPostLike cpl " +
            "WHERE cpl.userId = :userId " +
            "AND cpl.post.id IN :postIds")
    List<Long> findLikedPostIds(@Param("userId") Long userId,
                                @Param("postIds") List<Long> postIds);

    // 단일 게시글 좋아요 여부 확인
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 좋아요 조회 (토글 기능용)
    Optional<CommunityPostLike> findByPostIdAndUserId(Long postId, Long userId);
}