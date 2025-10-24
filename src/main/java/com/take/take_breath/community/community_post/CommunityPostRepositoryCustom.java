package com.take.take_breath.community.community_post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommunityPostRepositoryCustom {

    // 전체 조회 (category, comments 포함)
    Page<CommunityPost> findAllWithCategoryAndComments(Pageable pageable);

    // 상세 조회 (comments 포함)
    Optional<CommunityPost> findByIdWithComments(Long postId);

    // 검색 (키워드, 카테고리)
    Page<CommunityPost> findBySearchOption(CommunityPostRequest.SearchDTO searchDTO, Pageable pageable);

    // 삭제되지 않은 글만 조회
    List<CommunityPost> findAllActive();

    // userId로 게시글 목록 조회
    Page<CommunityPost> findByUserId(Long userId, Pageable pageable);

    // 소프트 삭제
    int softDelete(Long id);

}