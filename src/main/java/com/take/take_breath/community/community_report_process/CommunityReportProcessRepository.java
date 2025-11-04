package com.take.take_breath.community.community_report_process;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityReportProcessRepository extends JpaRepository<CommunityReportProcess, Long> {

    /**
     * 커뮤니티 신고 상태별 카운트 조회
     */
    @Query(value = """
        SELECT status, COUNT(*) as count 
        FROM community_report_process_tb 
        GROUP BY status
        """, nativeQuery = true)
    List<Object[]> countByStatus();

    /**
     * 최근 N개월간 월별 커뮤니티 신고 처리 건수
     */
    @Query(value = """
        SELECT 
            DATE_FORMAT(created_at, '%Y-%m') as month,
            COUNT(*) as count
        FROM community_report_process_tb
        WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL :months MONTH)
        GROUP BY DATE_FORMAT(created_at, '%Y-%m')
        ORDER BY month ASC
        """, nativeQuery = true)
    List<Object[]> getMonthlyReportProcessCount(@Param("months") int months);

    /**
     * 각 상태별 최신 처리 건 조회 (대시보드용)
     */
    @Query(value = """
        SELECT status, COUNT(DISTINCT report_id) as count
        FROM community_report_process_tb crp
        WHERE crp.created_at = (
            SELECT MAX(crp2.created_at)
            FROM community_report_process_tb crp2
            WHERE crp2.report_id = crp.report_id
        )
        GROUP BY status
        """, nativeQuery = true)
    List<Object[]> countLatestStatusByReport();
}
