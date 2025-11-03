package com.take.take_breath.community.community_report;

import com.take.take_breath.members.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityReportRepository extends JpaRepository<CommunityReport, Long> {

    boolean existsByReporterIdAndPostId(Long reporterId, Long postId);

    List<CommunityReport> findByPostIdAndStatus(Long postId, CommunityReportStatus status);

    Page<CommunityReport> findAllByReporterIdOrderByCreatedAtDesc(Long reporterId, Pageable pageable);

    @Query("SELECT r FROM CommunityReport r " +
            "LEFT JOIN FETCH r.adminComments c " +
            "WHERE r.id = :reportId")
    Optional<CommunityReport> findByIdWithAdminComments(@Param("reportId") Long reportId);

    long countByStatus(CommunityReportStatus status);


    @Query("SELECT COUNT(DISTINCT r.post.id) FROM CommunityReport r " +
            "WHERE r.post.member = :member AND r.status = 'APPROVED'")
    long countDistinctByPostMemberAndStatus(@Param("member") Member member,
                                            @Param("status") CommunityReportStatus status);
}



