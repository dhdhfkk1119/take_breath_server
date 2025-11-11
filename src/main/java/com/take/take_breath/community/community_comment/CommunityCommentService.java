package com.take.take_breath.community.community_comment;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._exception.Exception404;
import com.take.take_breath.community.comment_report.CommentReport;
import com.take.take_breath.community.comment_report.CommentReportRepository;
import com.take.take_breath.community.comment_report_process.CommentReportProcess;
import com.take.take_breath.community.comment_report_process.CommentReportProcessRepository;
import com.take.take_breath.community.community_event.CommentCreatedEvent;
import com.take.take_breath.community.community_post.CommunityPost;
import com.take.take_breath.community.community_post.CommunityPostRepository;
import com.take.take_breath.community.community_post.CommunityPostResponse;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityPostRepository communityPostRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CommentReportProcessRepository commentReportProcessRepository;
    private final CommentReportRepository commentReportRepository;

    /**
     * 댓글 작성
     */
    @Transactional
    public CommunityCommentResponse.ResponseDTO saveComment(Long postId, CommunityCommentRequest.SaveDTO saveDTO, Long currentMemberId) {

        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다. ID: " + postId));

        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new Exception404("회원을 찾을 수 없습니다. ID: " + currentMemberId));

        if (post.isDeleted()) {
            throw new Exception400("삭제된 게시글에는 댓글을 작성할 수 없습니다.");
        }

        CommunityComment comment = CommunityComment.builder()
                .content(saveDTO.getContent())
                .post(post)
                .member(member)
                .build();

        CommunityComment savedComment = communityCommentRepository.save(comment);

        eventPublisher.publishEvent(
                new CommentCreatedEvent(post.getMember().getId(), post.getTitle(), currentMemberId, member.getNickName())
        );

        post.addComment(savedComment);

        log.info("[댓글 작성] commentId={}, postId={}, memberId={}", savedComment.getId(), postId, currentMemberId);
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
     * 사용자별 게시글 조회 (좋아요 여부 포함)
     */
    public Page<CommunityCommentResponse.ResponseDTO> findCommentByMemberId(Long targetMemberId, Pageable pageable) {

        Page<CommunityComment> comments = communityCommentRepository.findByMemberId(targetMemberId, pageable);

        return comments.map(comment -> CommunityCommentResponse.ResponseDTO.builder()
                .comment(comment)
                .build());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommunityCommentResponse.ResponseDTO updateComment(Long commentId, CommunityCommentRequest.UpdateDTO updateDTO, Long currentMemberId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new Exception404("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (!comment.isOwner(currentMemberId)) {
            throw new Exception403("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        if (comment.isDeleted()) {
            throw new Exception400("삭제된 댓글은 수정할 수 없습니다.");
        }

        comment.update(updateDTO.getContent());

        log.info("[댓글 수정] commentId={}, memberId={}", commentId, currentMemberId);
        return new CommunityCommentResponse.ResponseDTO(comment);
    }

    /**
     * 댓글 삭제 (Soft Delete)
     */
    @Transactional
    public void deleteComment(Long commentId, Long currentMemberId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new Exception404("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (!comment.isOwner(currentMemberId)) {
            throw new Exception403("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        if (comment.isDeleted()) {
            throw new Exception400("이미 삭제된 댓글입니다.");
        }

        comment.softDelete();

        log.info("[댓글 삭제] commentId={}, memberId={}", commentId, currentMemberId);
    }

    /**
     * 관리자 전용: 댓글 강제 삭제
     */
    @Transactional
    public void forceDeleteComment(Long commentId, String reason, Long adminId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new Exception404("댓글을 찾을 수 없습니다."));

        if (comment.isDeleted()) {
            throw new Exception400("이미 삭제 처리된 댓글입니다. commentId: " + commentId);
        }

        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new Exception404("관리자 정보를 찾을 수 없습니다. adminId: " + adminId));

        comment.softDelete();
        comment.increaseReportCount();

        List<CommentReport> pendingReports = commentReportRepository
                .findByCommentIdAndStatus(commentId, CommunityReportStatus.PENDING);

        pendingReports.forEach(report -> {
            report.setStatus(CommunityReportStatus.APPROVED);

            CommentReportProcess process = CommentReportProcess.builder()
                    .report(report)
                    .admin(admin)
                    .status(CommunityReportStatus.APPROVED)
                    .adminComment("댓글 강제 삭제로 인한 자동 승인")
                    .build();
            commentReportProcessRepository.save(process);
        });

        log.warn("[관리자 강제 삭제] commentId={}, reason={}, 자동 처리된 신고 수={}",
                commentId, reason, pendingReports.size());
    }
}