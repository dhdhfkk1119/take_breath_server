package com.take.take_breath.community.community_post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Optional<CommunityPost> findByTitle(String title);

    // 카테고리 ID로 게시글 개수 조회 (삭제되지 않은 게시글만)
    @Query("SELECT COUNT(p) FROM CommunityPost p WHERE p.category.id = :categoryId AND p.deletedAt IS NULL")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}
