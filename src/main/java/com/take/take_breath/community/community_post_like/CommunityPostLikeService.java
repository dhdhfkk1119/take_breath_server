package com.take.take_breath.community.community_post_like;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath.community.community_event.PostLikeEvent;
import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.community.community_post.CommunityPostRepository;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 좋아요 토글 (좋아요/취소)
     */
    @Transactional
    public CommunityPostLikeResponse.ResponseDTO toggleLike(Long postId, Long currentMemberId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new Exception400("게시글을 찾을 수 없습니다. ID: " + postId));

        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new Exception400("회원을 찾을 수 없습니다. ID: " + currentMemberId));

        if (post.isDeleted()) {
            throw new Exception400("삭제된 게시글에는 좋아요를 할 수 없습니다.");
        }

        Optional<CommunityPostLike> existingLike = communityPostLikeRepository.findByPostIdAndMemberId(postId, currentMemberId);

        boolean liked;
        if (existingLike.isPresent()) {
            // 좋아요 취소
            communityPostLikeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            liked = false;
            log.info("[좋아요 취소] postId={}, currentMemberId={}", postId, currentMemberId);
        } else {
            // 좋아요 추가
            CommunityPostLike like = CommunityPostLike.builder()
                    .post(post)
                    .member(member)
                    .build();
            communityPostLikeRepository.save(like);
            post.increaseLikeCount();
            liked = true;

            eventPublisher.publishEvent(
                    new PostLikeEvent(post.getMember().getId(), post.getTitle(), currentMemberId, member.getNickName(), postId)
            );

            log.info("[좋아요 추가] postId={}, currentMemberId={}", postId, currentMemberId);
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
    public boolean isLiked(Long postId, Long memberId) {
        return communityPostLikeRepository.existsByPostIdAndMemberId(postId, memberId);
    }
}