package com.take.take_breath.community.community_post_like;

import com.take.take_breath.community.community_event.PostLikeEvent;
import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.community.community_post.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityPostLikeService {

    private final CommunityPostLikeRepository communityPostLikeRepository;
    private final CommunityPostRepository communityPostRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 좋아요 토글 (좋아요/취소)
     */
    @Transactional
    public CommunityPostLikeResponse.ResponseDTO toggleLike(Long postId, Long currentUserId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        if (post.isDeleted()) {
            throw new IllegalArgumentException("삭제된 게시글에는 좋아요를 할 수 없습니다.");
        }

        Optional<CommunityPostLike> existingLike = communityPostLikeRepository.findByPostIdAndUserId(postId, currentUserId);

        boolean liked;
        if (existingLike.isPresent()) {
            // 좋아요 취소
            communityPostLikeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            liked = false;
            log.info("[좋아요 취소] postId={}, userId={}", postId, currentUserId);
        } else {
            // 좋아요 추가
            CommunityPostLike like = CommunityPostLike.builder()
                    .post(post)
                    .userId(currentUserId)  // TODO: User 엔티티 연동 후 수정
                    .build();
            communityPostLikeRepository.save(like);
            post.increaseLikeCount();
            liked = true;

            eventPublisher.publishEvent(
                    new PostLikeEvent(post.getUserId(), post.getTitle(), currentUserId)
            );

            log.info("[좋아요 추가] postId={}, userId={}", postId, currentUserId);
        }

        return new CommunityPostLikeResponse.ResponseDTO(liked, (long) post.getLikeCount());
    }

    /**
     * 게시글 좋아요 수 조회
     */
    public Long getLikeCount(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        return (long) post.getLikeCount();
    }

    /**
     * 좋아요 여부 확인
     */
    public boolean isLiked(Long postId, Long userId) {
        return communityPostLikeRepository.existsByPostIdAndUserId(postId, userId);
    }
}