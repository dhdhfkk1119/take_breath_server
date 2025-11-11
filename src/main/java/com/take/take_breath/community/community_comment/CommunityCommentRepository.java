package com.take.take_breath.community.community_comment;

import com.take.take_breath.community.community_post.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    List<CommunityComment> findByPostId(Long postId);


    Page<CommunityComment> findByMemberId(Long memberId, Pageable pageable);


    @Query("SELECT c FROM CommunityComment c WHERE c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<CommunityComment> findAllActive();
}
