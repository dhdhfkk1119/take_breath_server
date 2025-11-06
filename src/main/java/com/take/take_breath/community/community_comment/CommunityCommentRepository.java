package com.take.take_breath.community.community_comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    List<CommunityComment> findByPostId(Long postId);

    @Query("SELECT c FROM CommunityComment c WHERE c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<CommunityComment> findAllActive();
}
