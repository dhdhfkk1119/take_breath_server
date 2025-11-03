package com.take.take_breath.admin.view.dto;

import com.take.take_breath.community.comment_report.CommentReportRepository;
import com.take.take_breath.community.community_report.CommunityReportRepository;
import com.take.take_breath.community.community_report.CommunityReportStatus;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private long totalMembers;
    private long activeMembers;
    private long suspendedMembers;
    private long withdrawalMembers;

    private long totalCounselors;
    private long activeCounselors;
    private long pendingCounselors;

    private long userCount;
    private long counselorCount;
    private long adminCount;

    private long totalReports;
    private long pendingReports;
    private long approvedReports;
    private long rejectedReports;
    private long postReports;
    private long commentReports;
    private MemberGrowthData memberGrowthData;

    private long helpButtonClicks;

    /**
     * 정적 팩토리 메서드 - Repository에서 통계 데이터 생성
     */
    public static DashboardStatsResponse from(MemberRepository memberRepository,
                                              CounselorRepository counselorRepository,
                                              CommunityReportRepository communityReportRepository,
                                              CommentReportRepository commentReportRepository) {
        // 회원 통계
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(Status.ACTIVE);
        long suspendedMembers = memberRepository.countByStatus(Status.SUSPENDED);
        long withdrawalMembers = memberRepository.countByStatus(Status.WITHDRAWAL);

        // 상담사 통계
        long totalCounselors = counselorRepository.count();
        long activeCounselors = counselorRepository.countByStatus(Status.ACTIVE);
        long pendingCounselors = counselorRepository.countByStatus(Status.PENDING);

        // 역할별 통계
        long userCount = memberRepository.countByRole(Role.USER);
        long counselorCount = memberRepository.countByRole(Role.COUNSELOR);
        long adminCount = memberRepository.countByRole(Role.ADMIN);

        // 🚨 신고 통계 계산
        long postReports = communityReportRepository.count();
        long commentReports = commentReportRepository.count();
        long totalReports = postReports + commentReports;

        long pendingPostReports = communityReportRepository.countByStatus(CommunityReportStatus.PENDING);
        long pendingCommentReports = commentReportRepository.countByStatus(CommunityReportStatus.PENDING);
        long pendingReports = pendingPostReports + pendingCommentReports;

        long approvedPostReports = communityReportRepository.countByStatus(CommunityReportStatus.APPROVED);
        long approvedCommentReports = commentReportRepository.countByStatus(CommunityReportStatus.APPROVED);
        long approvedReports = approvedPostReports + approvedCommentReports;

        long rejectedPostReports = communityReportRepository.countByStatus(CommunityReportStatus.REJECTED);
        long rejectedCommentReports = commentReportRepository.countByStatus(CommunityReportStatus.REJECTED);
        long rejectedReports = rejectedPostReports + rejectedCommentReports;

        // 도우미 버튼 클릭 수 (추후 구현)
        long helpButtonClicks = 0L;

        return DashboardStatsResponse.builder()
                .totalMembers(totalMembers)
                .activeMembers(activeMembers)
                .suspendedMembers(suspendedMembers)
                .withdrawalMembers(withdrawalMembers)
                .totalCounselors(totalCounselors)
                .activeCounselors(activeCounselors)
                .pendingCounselors(pendingCounselors)
                .userCount(userCount)
                .counselorCount(counselorCount)
                .adminCount(adminCount)
                .totalReports(totalReports)
                .pendingReports(pendingReports)
                .approvedReports(approvedReports)
                .rejectedReports(rejectedReports)
                .postReports(postReports)
                .commentReports(commentReports)
                .helpButtonClicks(0L)
                .build();
    }
}