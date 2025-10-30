package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.DashboardStatsDTO;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final MemberRepository memberRepository;
    private final CounselorRepository counselorRepository;

    public DashboardStatsDTO getDashboardStats() {
        // 회원 통계
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(Status.ACTIVE);
        long suspendedMembers = memberRepository.countByStatus(Status.SUSPENDED);

        // 상담사 통계
        long totalCounselors = counselorRepository.count();
        long activeCounselors = counselorRepository.countByStatus(Status.ACTIVE);
        long pendingCounselors = counselorRepository.countByStatus(Status.PENDING);

        // 역할별 통계
        long userCount = memberRepository.countByRole(Role.USER);
        long counselorCount = memberRepository.countByRole(Role.COUNSELOR);
        long adminCount = memberRepository.countByRole(Role.ADMIN);

        return new DashboardStatsDTO(
                totalMembers,
                activeMembers,
                suspendedMembers,
                totalCounselors,
                activeCounselors,
                pendingCounselors,
                userCount,
                counselorCount,
                adminCount
        );
    }
}