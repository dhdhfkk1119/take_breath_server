package com.take.take_breath.admin.view.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    // 회원 통계
    private long totalMembers;          // 전체 회원
    private long activeMembers;         // 활성 회원
    private long suspendedMembers;      // 정지 회원

    // 상담사 통계
    private long totalCounselors;       // 전체 상담사
    private long activeCounselors;      // 활성 상담사
    private long pendingCounselors;     // 승인 대기

    // 역할별 통계
    private long userCount;             // 일반 회원
    private long counselorCount;        // 상담사
    private long adminCount;            // 관리자
}