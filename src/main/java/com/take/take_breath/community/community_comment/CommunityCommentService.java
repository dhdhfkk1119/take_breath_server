package com.take.take_breath.community.community_comment;

import com.take.take_breath.community.comment_report.CommentReport;
import com.take.take_breath.community.comment_report.CommentReportRepository;
import com.take.take_breath.community.comment_report_process.CommentReportProcess;
import com.take.take_breath.community.comment_report_process.CommentReportProcessRepository;
import com.take.take_breath.community.community_event.CommentCreatedEvent;
import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.community.community_post.CommunityPostRepository;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityPostRepository communityPostRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CommentReportProcessRepository commentReportProcessRepository;
    private final CommentReportRepository commentReportRepository;

    /**
     * 댓글 작성
     */
    @Transactional
    public CommunityCommentResponse.ResponseDTO saveComment(Long postId, CommunityCommentRequest.SaveDTO saveDTO, Long currentUserId) {

        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        if (post.isDeleted()) {
            throw new IllegalArgumentException("삭제된 게시글에는 댓글을 작성할 수 없습니다.");
        }

        CommunityComment comment = CommunityComment.builder()
                .content(saveDTO.getContent())
                .post(post)
                .userId(currentUserId)
                .build();

        CommunityComment savedComment = communityCommentRepository.save(comment);

        eventPublisher.publishEvent(
                new CommentCreatedEvent(post.getUserId(), post.getTitle(), currentUserId)
        );

        post.addComment(savedComment);

        log.info("[댓글 작성] commentId={}, postId={}, userId={}", savedComment.getId(), postId, currentUserId);
        return new CommunityCommentResponse.ResponseDTO(savedComment);
    }

    /**
     * 게시글의 댓글 목록 조회
     */
    public List<CommunityCommentResponse.ResponseDTO> findCommentsByPostId(Long postId) {
        return communityCommentRepository.findByPostId(postId).stream()
                .map(comment -> new CommunityCommentResponse.ResponseDTO(comment))
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommunityCommentResponse.ResponseDTO updateComment(Long commentId, CommunityCommentRequest.UpdateDTO updateDTO, Long currentUserId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (!comment.isOwner(currentUserId)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        if (comment.isDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글은 수정할 수 없습니다.");
        }

        comment.update(updateDTO.getContent());

        log.info("[댓글 수정] commentId={}, userId={}", commentId, currentUserId);
        return new CommunityCommentResponse.ResponseDTO(comment);
    }

    /**
     * 댓글 삭제 (Soft Delete)
     */
    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (!comment.isOwner(currentUserId)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        if (comment.isDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 댓글입니다.");
        }

        comment.softDelete();

        log.info("[댓글 삭제] commentId={}, userId={}", commentId, currentUserId);
    }

    /**
     * 관리자 전용: 댓글 강제 삭제
     */
    @Transactional
    public void forceDeleteComment(Long commentId, String reason, Long adminId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (comment.isDeleted()) {
            // 이미 삭제된 댓글이라면 불필요한 재처리 없이 예외를 던져 종료
            throw new IllegalArgumentException("이미 삭제 처리된 댓글입니다. commentId: " + commentId);
        }

        comment.softDelete();
        comment.increaseReportCount();

        // 해당 댓글의 모든 PENDING 신고를 APPROVED로 변경
        List<CommentReport> pendingReports = commentReportRepository
                .findByCommentIdAndStatus(commentId, CommunityReportStatus.PENDING);

        pendingReports.forEach(report -> {
            report.setStatus(CommunityReportStatus.APPROVED);

            CommentReportProcess process = CommentReportProcess.builder()
                    .report(report)
                    .adminId(adminId)
                    .status(CommunityReportStatus.APPROVED)
                    .adminComment("댓글 강제 삭제로 인한 자동 승인")
                    .build();
            commentReportProcessRepository.save(process);
        });

        log.warn("[관리자 강제 삭제] commentId={}, reason={}, 자동 처리된 신고 수={}",
                commentId, reason, pendingReports.size());
    }
}