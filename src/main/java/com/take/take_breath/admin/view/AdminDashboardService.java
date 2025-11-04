package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.DashboardStatsResponse;
import com.take.take_breath.admin.view.dto.MemberGrowthData;
import com.take.take_breath.community.comment_report.CommentReportRepository;
import com.take.take_breath.community.community_report.CommunityReportRepository;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
}
