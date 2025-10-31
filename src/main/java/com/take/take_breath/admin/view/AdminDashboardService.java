package com.take.take_breath.admin.view;

import com.take.take_breath.admin.view.dto.DashboardStatsResponse;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final MemberRepository memberRepository;
    private final CounselorRepository counselorRepository;

    public DashboardStatsResponse getDashboardStats() {
        return DashboardStatsResponse.from(memberRepository, counselorRepository);
    }
}