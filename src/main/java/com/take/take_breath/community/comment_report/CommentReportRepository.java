package com.take.take_breath.community.comment_report;

import com.take.take_breath.community.community_report.CommunityReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    boolean existsByReporterIdAndCommentId(Long reporterId, Long commentId);

    List<CommentReport> findByCommentIdAndStatus(Long commentId, CommunityReportStatus status);

    Page<CommentReport> findAllByReporterIdOrderByCreatedAtDesc(Long reporterId, Pageable pageable);

    @Query("SELECT r FROM CommentReport r " +
            "LEFT JOIN FETCH r.adminComments c " +
            "WHERE r.id = :reportId")
    Optional<CommentReport> findByIdWithAdminComments(@Param("reportId") Long reportId);
}