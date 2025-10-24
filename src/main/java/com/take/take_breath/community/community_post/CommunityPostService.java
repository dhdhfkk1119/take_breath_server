package com.take.take_breath.community.community_post;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /**
     * 전체 게시글 조회 (좋아요 여부 포함)
     */
    public Page<CommunityPostResponse.ListDTO> findAllPosts(Long currentUserId, Pageable pageable) {
        // 1. 게시글 조회
        Page<CommunityPost> posts = communityPostRepositoryCustom.findAllWithCategoryAndComments(pageable);

        // 2. 게시글 ID 목록 추출
        List<Long> postIds = posts.stream()
                .map(post -> post.getId())
                .collect(Collectors.toList());

        // 3. 이 사용자가 좋아요 누른 게시글 ID들 조회
        List<Long> likedPostIdsList = communityPostLikeRepository.findLikedPostIds(currentUserId, postIds);
        java.util.Set<Long> likedPostIds = new java.util.HashSet<>(likedPostIdsList);

        // 4. DTO 변환 시 liked 값 전달
        return posts.map(post -> new CommunityPostResponse.ListDTO(post, likedPostIds.contains(post.getId())));
    }

    /**
     * 게시글 상세 조회 (조회수 증가, 좋아요 여부 포함)
     */
    @Transactional
    public CommunityPostResponse.DetailDTO findPostDetail(Long postId, String commentSortType, Long currentUserId) {
        CommunityPost post = communityPostRepositoryCustom.findByIdWithComments(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        if (post.isDeleted()) {
            throw new IllegalArgumentException("삭제된 게시글입니다.");
        }

        // 좋아요 여부 확인
        boolean liked = communityPostLikeRepository.existsByPostIdAndUserId(postId, currentUserId);

        // 조회수 증가
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
    public CommunityPostResponse.ResponseDTO savePost(CommunityPostRequest.SaveDTO saveDTO, Long currentUserId) {

        // 카테고리 조회
        CommunityCategory category = null;
        if (saveDTO.getCategoryId() != null) {
            category = communityCategoryRepository.findById(saveDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        }

        // 엔티티 생성
        CommunityPost post = CommunityPost.builder()
                .title(saveDTO.getTitle())
                .content(saveDTO.getContent())
                .userId(currentUserId)
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
    public CommunityPostResponse.ResponseDTO updatePost(Long postId, CommunityPostRequest.UpdateDTO updateDTO, Long currentUserId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.isOwner(currentUserId)) {
            throw new IllegalArgumentException("본인이 작성한 게시글만 수정할 수 있습니다.");
        }

        if (post.isDeleted()) {
            throw new IllegalArgumentException("삭제된 게시글은 수정할 수 없습니다.");
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
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
            post.setCategory(category);
        }

        // 이미지 삭제
        if (updateDTO.getDeleteImageIds() != null && !updateDTO.getDeleteImageIds().isEmpty()) {
            for (Long imageId : updateDTO.getDeleteImageIds()) {
                CommunityPostImage image = communityPostImageRepository.findById(imageId)
                        .orElseThrow(() -> new IllegalArgumentException("삭제할 이미지를 찾을 수 없습니다."));

                if (!image.getPost().getId().equals(postId)) {
                    throw new IllegalArgumentException("해당 게시글의 이미지가 아닙니다.");
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
    public void deletePost(Long postId, Long currentUserId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.isOwner(currentUserId)) {
            throw new IllegalArgumentException("본인이 작성한 게시글만 삭제할 수 있습니다.");
        }

        if (post.isDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 게시글입니다.");
        }

        post.softDelete();
    }

    /**
     * 게시글 검색 (동적 쿼리, 좋아요 여부 포함)
     */
    public Page<CommunityPostResponse.ListDTO> searchPosts(
            Long currentUserId,
            CommunityPostRequest.SearchDTO searchDTO,
            Pageable pageable) {

        // 정렬 조건 설정
        Pageable sortedPageable = pageable;
        String sortType = searchDTO.getSortType();

        if ("LATEST".equals(sortType)) {
            sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt"));
        } else if ("LIKES".equals(sortType)) {
            sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "likeCount"));
        } else if ("VIEWS".equals(sortType)) {
            sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "viewCount"));
        }

        Page<CommunityPost> posts = communityPostRepositoryCustom.findBySearchOption(searchDTO, sortedPageable);

        // 게시글 ID 목록 추출
        List<Long> postIds = posts.stream()
                .map(post -> post.getId())
                .collect(Collectors.toList());

        // 좋아요 누른 게시글 ID들 조회
        List<Long> likedPostIdsList = communityPostLikeRepository.findLikedPostIds(currentUserId, postIds);
        java.util.Set<Long> likedPostIds = new java.util.HashSet<>(likedPostIdsList);

        return posts.map(post -> new CommunityPostResponse.ListDTO(post, likedPostIds.contains(post.getId())));
    }


    /**
     * 사용자별 게시글 조회 (좋아요 여부 포함)
     */
    public Page<CommunityPostResponse.ListDTO> findPostsByUserId(Long userId, Long currentUserId, Pageable pageable) {
        Page<CommunityPost> posts = communityPostRepositoryCustom.findByUserId(userId, pageable);

        // 게시글 ID 목록 추출
        List<Long> postIds = posts.stream()
                .map(post -> post.getId())
                .collect(Collectors.toList());

        // 좋아요 누른 게시글 ID들 조회
        List<Long> likedPostIdsList = communityPostLikeRepository.findLikedPostIds(currentUserId, postIds);
        java.util.Set<Long> likedPostIds = new java.util.HashSet<>(likedPostIdsList);

        return posts.map(post -> new CommunityPostResponse.ListDTO(post, likedPostIds.contains(post.getId())));
    }

    /**
     * 관리자 전용: 강제 삭제
     */
    @Transactional
    public void forceDeletePost(Long postId, String reason, Long adminId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (post.isDeleted()) {
            // 이미 삭제 처리된 게시글이라면, 불필요한 재처리 없이 예외를 던져 종료
            throw new IllegalArgumentException("이미 삭제 처리된 게시글입니다. postId: " + postId);
        }

        post.softDelete();

        // 해당 게시글의 모든 PENDING 신고를 APPROVED로 변경
        List<CommunityReport> pendingReports = communityReportRepository
                .findByPostIdAndStatus(postId, CommunityReportStatus.PENDING);

        pendingReports.forEach(report -> {
            report.setStatus(CommunityReportStatus.APPROVED);

            // 신고 처리 기록 생성
            CommunityReportProcess process = CommunityReportProcess.builder()
                    .report(report)
                    .adminId(adminId)
                    .status(CommunityReportStatus.APPROVED)
                    .adminComment("게시글 강제 삭제로 인한 자동 승인")
                    .build();
            communityReportProcessRepository.save(process);
        });

        log.warn("[관리자 강제 삭제] postId={}, reason={}, 자동 처리된 신고 수={}",
                postId, reason, pendingReports.size());
    }

}