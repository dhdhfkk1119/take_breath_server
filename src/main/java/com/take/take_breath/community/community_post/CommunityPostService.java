package com.take.take_breath.community.community_post;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath.community.community_category.CommunityCategory;
import com.take.take_breath.community.community_category.CommunityCategoryRepository;
import com.take.take_breath.community.community_post_image.CommunityPostImage;
import com.take.take_breath.community.community_post_image.CommunityPostImageRepository;
import com.take.take_breath.community.community_post_like.CommunityPostLikeRepository;
import com.take.take_breath.community.community_report.CommunityReport;
import com.take.take_breath.community.community_report.CommunityReportRepository;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import com.take.take_breath.community.community_report_process.CommunityReportProcess;
import com.take.take_breath.community.community_report_process.CommunityReportProcessRepository;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityPostRepositoryCustom communityPostRepositoryCustom;
    private final CommunityCategoryRepository communityCategoryRepository;
    private final CommunityPostImageRepository communityPostImageRepository;
    private final CommunityPostLikeRepository communityPostLikeRepository;
    private final CommunityReportProcessRepository communityReportProcessRepository;
    private final CommunityReportRepository communityReportRepository;
    private final MemberRepository memberRepository;

    /**
     * 게시글 목록 조회/검색 (키워드, 카테고리, 정렬, 좋아요 여부 포함)
     */
    public Page<CommunityPostResponse.ListDTO> searchPosts(
            Long currentUserId,
            CommunityPostRequest.SearchDTO searchDTO,
            Pageable pageable) {

        Pageable sortedPageable = pageable;
        CommunityPostSortType sortType = searchDTO.getSortType();

        if (sortType != null) {
            Sort sort = switch (sortType) {
                case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
                case LIKES -> Sort.by(Sort.Direction.DESC, "likeCount");
                case VIEWS -> Sort.by(Sort.Direction.DESC, "viewCount");
            };
            sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        boolean hasSearchCondition = (searchDTO.getKeyword() != null && !searchDTO.getKeyword().trim().isEmpty())
                || (searchDTO.getCategoryIds() != null && !searchDTO.getCategoryIds().isEmpty());

        Page<CommunityPost> posts;

        if (hasSearchCondition) {
            // 검색 조건이 있으면 findBySearchOption 사용
            posts = communityPostRepositoryCustom.findBySearchOption(searchDTO, sortedPageable);
        } else {
            // 검색 조건이 없으면 findAllWithCategoryAndComments 사용
            posts = communityPostRepositoryCustom.findAllWithCategoryAndComments(sortedPageable);
        }

        List<Long> postIds = posts.stream()
                .map(post -> post.getId())
                .collect(Collectors.toList());

        // 좋아요 여부 조회
        List<Long> likedPostIdsList = communityPostLikeRepository.findLikedPostIds(currentUserId, postIds);
        java.util.Set<Long> likedPostIds = new java.util.HashSet<>(likedPostIdsList);

        // 댓글 개수 조회
        Map<Long, Long> commentCounts = communityPostRepositoryCustom.getCommentCountsByPostIds(postIds);

        return posts.map(post -> CommunityPostResponse.ListDTO.builder()
                .post(post)
                .liked(likedPostIds.contains(post.getId()))
                .commentCount(commentCounts.getOrDefault(post.getId(), 0L).intValue())
                .build());
    }

    /**
     * 게시글 상세 조회 (조회수 증가, 좋아요 여부 포함)
     */
    @Transactional
    public CommunityPostResponse.DetailDTO findPostDetail(Long postId, String commentSortType, Long memberId) {
        CommunityPost post = communityPostRepositoryCustom.findByIdWithComments(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다. ID: " + postId));

        if (post.isDeleted()) {
            throw new Exception400("삭제된 게시글입니다.");
        }

        // 좋아요 여부 확인
        boolean liked = communityPostLikeRepository.existsByPostIdAndMemberId(postId, memberId);

        post.increaseViewCount();

        return CommunityPostResponse.DetailDTO.builder()
                .post(post)
                .commentSortType(commentSortType)
                .liked(liked)
                .build();
    }

    /**
     * 게시글 작성
     */
    @Transactional
    public CommunityPostResponse.ResponseDTO savePost(CommunityPostRequest.SaveDTO saveDTO, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception404("회원을 찾을 수 없습니다."));

        // 카테고리 조회
        CommunityCategory category = null;
        if (saveDTO.getCategoryId() != null) {
            category = communityCategoryRepository.findById(saveDTO.getCategoryId())
                    .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));
        }

        // 엔티티 생성
        CommunityPost post = CommunityPost.builder()
                .title(saveDTO.getTitle())
                .content(saveDTO.getContent())
                .member(member)
                .category(category)
                .build();

        CommunityPost savedPost = communityPostRepository.save(post);

        // 이미지 저장
        if (saveDTO.getImageUrls() != null && !saveDTO.getImageUrls().isEmpty()) {
            for (String imageUrl : saveDTO.getImageUrls()) {
                CommunityPostImage image = CommunityPostImage.builder()
                        .imageUrl(imageUrl)
                        .post(savedPost)
                        .build();
                communityPostImageRepository.save(image);
                savedPost.addImage(image);
            }
        }

        return CommunityPostResponse.ResponseDTO.builder()
                .post(savedPost)
                .build();
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public CommunityPostResponse.ResponseDTO updatePost(Long postId, CommunityPostRequest.UpdateDTO updateDTO, Long memberId) {
        // 커스텀 메서드 사용으로 변경 (최소한의 연관관계만 조회)
        CommunityPost post = communityPostRepositoryCustom.findByIdWithCategory(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        if (!post.isOwner(memberId)) {
            throw new Exception403("본인이 작성한 게시글만 수정할 수 있습니다.");
        }

        if (post.isDeleted()) {
            throw new Exception400("삭제된 게시글은 수정할 수 없습니다.");
        }

        // 제목, 내용 수정
        if (updateDTO.getTitle() != null && !updateDTO.getTitle().trim().isEmpty()) {
            post.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getContent() != null && !updateDTO.getContent().trim().isEmpty()) {
            post.setContent(updateDTO.getContent());
        }

        // 카테고리 수정
        if (updateDTO.getCategoryId() != null) {
            CommunityCategory category = communityCategoryRepository.findById(updateDTO.getCategoryId())
                    .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));
            post.setCategory(category);
        }

        // 이미지 삭제
        if (updateDTO.getDeleteImageIds() != null && !updateDTO.getDeleteImageIds().isEmpty()) {
            for (Long imageId : updateDTO.getDeleteImageIds()) {
                CommunityPostImage image = communityPostImageRepository.findById(imageId)
                        .orElseThrow(() -> new Exception404("삭제할 이미지를 찾을 수 없습니다."));

                if (!image.getPost().getId().equals(postId)) {
                    throw new Exception400("해당 게시글의 이미지가 아닙니다.");
                }

                post.getImages().remove(image);
                communityPostImageRepository.delete(image);
            }
        }

        // 이미지 추가
        if (updateDTO.getAddImageUrls() != null && !updateDTO.getAddImageUrls().isEmpty()) {
            for (String imageUrl : updateDTO.getAddImageUrls()) {
                CommunityPostImage image = CommunityPostImage.builder()
                        .imageUrl(imageUrl)
                        .post(post)
                        .build();
                communityPostImageRepository.save(image);
                post.addImage(image);
            }
        }

        return CommunityPostResponse.ResponseDTO.builder()
                .post(post)
                .build();
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    @Transactional
    public void deletePost(Long postId, Long memberId) {
        // 커스텀 메서드 사용으로 변경
        CommunityPost post = communityPostRepositoryCustom.findByIdWithCategory(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        if (!post.isOwner(memberId)) {
            throw new Exception403("본인이 작성한 게시글만 삭제할 수 있습니다.");
        }

        if (post.isDeleted()) {
            throw new Exception400("이미 삭제된 게시글입니다.");
        }

        post.softDelete();
    }

    /**
     * 사용자별 게시글 조회 (좋아요 여부 포함)
     */
    public Page<CommunityPostResponse.ListDTO> findPostsByMemberId(Long targetMemberId, Long memberId, Pageable pageable) {
        Page<CommunityPost> posts = communityPostRepositoryCustom.findByMemberId(targetMemberId, pageable);

        // 게시글 ID 목록 추출 - 람다 표현식 사용
        List<Long> postIds = posts.stream()
                .map(post -> post.getId())
                .collect(Collectors.toList());

        // 좋아요 누른 게시글 IDs 조회
        List<Long> likedPostIdsList = communityPostLikeRepository.findLikedPostIds(memberId, postIds);
        java.util.Set<Long> likedPostIds = new java.util.HashSet<>(likedPostIdsList);

        // 댓글 개수 조회
        Map<Long, Long> commentCounts = communityPostRepositoryCustom.getCommentCountsByPostIds(postIds);

        return posts.map(post -> CommunityPostResponse.ListDTO.builder()
                .post(post)
                .liked(likedPostIds.contains(post.getId()))
                .commentCount(commentCounts.getOrDefault(post.getId(), 0L).intValue())
                .build());
    }

    /**
     * 관리자 전용: 강제 삭제
     */
    @Transactional
    public void forceDeletePost(Long postId, String reason, Long adminId) {
        CommunityPost post = communityPostRepositoryCustom.findByIdWithCategory(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        if (post.isDeleted()) {
            throw new Exception400("이미 삭제 처리된 게시글입니다. postId: " + postId);
        }

        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new Exception404("관리자 정보를 찾을 수 없습니다. adminId: " + adminId));

        post.softDelete();

        // 해당 게시글의 모든 PENDING 신고를 APPROVED로 변경
        List<CommunityReport> pendingReports = communityReportRepository
                .findByPostIdAndStatus(post.getId(), CommunityReportStatus.PENDING);

        pendingReports.forEach(report -> {
            report.setStatus(CommunityReportStatus.APPROVED);

            CommunityReportProcess process = CommunityReportProcess.builder()
                    .report(report)
                    .admin(admin)
                    .status(CommunityReportStatus.APPROVED)
                    .adminComment("게시글 강제 삭제로 인한 자동 승인")
                    .build();
            communityReportProcessRepository.save(process);
        });

        log.warn("[관리자 강제 삭제] postId={}, reason={}, 자동 처리된 신고 수={}",
                postId, reason, pendingReports.size());
    }
}