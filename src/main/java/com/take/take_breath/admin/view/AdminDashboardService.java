package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.DashboardStatsResponse;
import com.take.take_breath.admin.view.dto.MemberGrowthData;
import com.take.take_breath.community.comment_report.CommentReportRepository;
import com.take.take_breath.community.community_report.CommunityReportRepository;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final MemberRepository memberRepository;
    private final CounselorRepository counselorRepository;
    private final CommunityReportRepository communityReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final JdbcTemplate jdbcTemplate;

    public DashboardStatsResponse getDashboardStats() {
        return DashboardStatsResponse.from(
                memberRepository,
                counselorRepository,
                communityReportRepository,
                commentReportRepository);
    }

    // 회원 증가 추이 데이터
    public MemberGrowthData getMemberGrowthData(int months) {

        List<Map<String, Object>> results = memberRepository.findMemberGrowthByMonths(months);

        // DTO로 변환
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        for (Map<String, Object> row : results) {
            labels.add((String) row.get("label"));
            data.add(((Number) row.get("total_members")).intValue());
        }

        return MemberGrowthData.builder()
                .labels(labels)
                .data(data)
                .build();
    }
}