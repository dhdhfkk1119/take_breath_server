package com.take.take_breath.members;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNameAndPhone(String name, String phone);

    boolean existsByEmail(String email);

    long countByStatus(Status status);

    List<Member> findByStatus(Status status);

    long countByRole(Role role);

    Optional<Member> findByProviderAndSocialId(String provider, String socialId);

    // 탈퇴하지않은 활성 회원만 조회
    @Query("SELECT m FROM Member m WHERE m.status IN ('ACTIVE', 'SUSPENDED', 'PENDING')")
    List<Member> findAllActiveMembers();

    // 월별 회원 증가 추이
    @Query(value = """
        WITH RECURSIVE months AS (
            SELECT 0 AS month_offset
            UNION ALL
            SELECT month_offset + 1
            FROM months
            WHERE month_offset < :months
        ),
        month_ends AS (
            SELECT 
                LAST_DAY(DATE_SUB(CURDATE(), INTERVAL month_offset MONTH)) AS month_date,
                month_offset
            FROM months
        )
        SELECT 
            DATE_FORMAT(me.month_date, '%Y년 %c월') AS label,
            SUM(CASE WHEN m.role = 'USER' THEN 1 ELSE 0 END) AS member_count,
            SUM(CASE WHEN m.role = 'COUNSELOR' THEN 1 ELSE 0 END) AS counselor_count
        FROM month_ends me
        LEFT JOIN member_tb m 
            ON m.created_at <= me.month_date
            AND m.status != 'WITHDRAWAL'
        GROUP BY me.month_date, me.month_offset
        ORDER BY me.month_offset DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findMemberGrowthByMonths(@Param("months") int months);

}
