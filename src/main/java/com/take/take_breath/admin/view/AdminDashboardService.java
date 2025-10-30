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
        return DashboardStatsDTO.from(memberRepository, counselorRepository);
    }
}