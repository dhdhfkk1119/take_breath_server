package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.DashboardStatsResponse;
import com.take.take_breath.admin.view.dto.MemberGrowthData;
import com.take.take_breath.admin.view.dto.ReportProcessData;
import com.take.take_breath.community.comment_report.CommentReportRepository;
import com.take.take_breath.community.comment_report_process.CommentReportProcessRepository;
import com.take.take_breath.community.community_report.CommunityReportRepository;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import com.take.take_breath.community.community_report_process.CommunityReportProcessRepository;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final MemberRepository memberRepository;
    private final CounselorRepository counselorRepository;
    private final CommunityReportRepository communityReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final CommunityReportProcessRepository communityReportProcessRepository;
    private final CommentReportProcessRepository commentReportProcessRepository;

    public DashboardStatsResponse getDashboardStats() {
        return DashboardStatsResponse.from(
                memberRepository,
                counselorRepository,
                communityReportRepository,
                commentReportRepository);
    }

    // 회원 증가 추이 데이터 (일반회원 + 상담사)
    public MemberGrowthData getMemberGrowthData(int months) {
        List<Map<String, Object>> results = memberRepository.findMemberGrowthByMonths(months);

        List<String> labels = new ArrayList<>();
        List<Integer> memberData = new ArrayList<>();
        List<Integer> counselorData = new ArrayList<>();

        for (Map<String, Object> row : results) {
            labels.add((String) row.get("label"));
            memberData.add(((Number) row.get("member_count")).intValue());
            counselorData.add(((Number) row.get("counselor_count")).intValue());
        }

        return MemberGrowthData.builder()
                .labels(labels)
                .memberData(memberData)
                .counselorData(counselorData)
                .build();
    }

    /**
     * 신고 처리 현황 추이 데이터 (커뮤니티 + 댓글 합산)
     * @param months 조회할 개월 수
     * @return 월별 신고 처리 건수 데이터
     */
    public ReportProcessData getReportProcessData(int months) {
        // 커뮤니티 신고 처리 월별 데이터
        List<Object[]> communityData = communityReportProcessRepository.getMonthlyReportProcessCount(months);

        // 댓글 신고 처리 월별 데이터
        List<Object[]> commentData = commentReportProcessRepository.getMonthlyReportProcessCount(months);

        // 월별로 데이터 합산
        Map<String, Integer> monthlyMap = new LinkedHashMap<>();

        // 커뮤니티 신고 집계
        for (Object[] row : communityData) {
            String month = (String) row[0];
            Integer count = ((Number) row[1]).intValue();
            monthlyMap.put(month, monthlyMap.getOrDefault(month, 0) + count);
        }

        // 댓글 신고 집계
        for (Object[] row : commentData) {
            String month = (String) row[0];
            Integer count = ((Number) row[1]).intValue();
            monthlyMap.put(month, monthlyMap.getOrDefault(month, 0) + count);
        }

        // 결과 리스트 생성
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : monthlyMap.entrySet()) {
            String month = entry.getKey();
            Integer count = entry.getValue();

            // 월 포맷 변환 (예: 2025-11 -> 2025년 11월)
            String[] parts = month.split("-");
            if (parts.length == 2) {
                String label = parts[0] + "년 " + Integer.parseInt(parts[1]) + "월";
                labels.add(label);
                data.add(count);
            }
        }

        return ReportProcessData.builder()
                .labels(labels)
                .data(data)
                .build();
    }

    /**
     * 신고 상태별 통계 (PENDING, IN_PROGRESS, COMPLETED, REJECTED)
     * @return 상태별 신고 건수
     */
//    public Map<String, Integer> getReportStatusStats() {
//        Map<String, Integer> statusStats = new LinkedHashMap<>();
//
//        // 커뮤니티 신고 상태별 카운트
//        List<Object[]> communityStatus = communityReportProcessRepository.countLatestStatusByReport();
//
//        // 댓글 신고 상태별 카운트
//        List<Object[]> commentStatus = commentReportProcessRepository.countLatestStatusByReport();
//
//        // 상태별 합산
//        for (Object[] row : communityStatus) {
//            String status = String.valueOf(row[0]);
//            Integer count = ((Number) row[1]).intValue();
//            statusStats.put(status, statusStats.getOrDefault(status, 0) + count);
//        }
//
//        for (Object[] row : commentStatus) {
//            String status = String.valueOf(row[0]);
//            Integer count = ((Number) row[1]).intValue();
//            statusStats.put(status, statusStats.getOrDefault(status, 0) + count);
//        }
//
//        return statusStats;
//    }
    public Map<String, Integer> getReportStatusStats() {
        Map<String, Integer> statusStats = new LinkedHashMap<>();

        // CommunityReportStatus enum의 모든 값에 대해 조회
        for (CommunityReportStatus status : CommunityReportStatus.values()) {
            long communityCount = communityReportRepository.countByStatus(status.name());
            long commentCount = commentReportRepository.countByStatus(status.name());

            int totalCount = (int)(communityCount + commentCount);
            statusStats.put(status.name(), totalCount);
        }

        return statusStats;
    }
}