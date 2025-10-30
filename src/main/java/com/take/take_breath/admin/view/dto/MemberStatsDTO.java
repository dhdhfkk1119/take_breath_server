package com.take.take_breath.admin.view.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberStatsDTO {
    private long totalMembers;       // 전체 회원
    private long activeMembers;      // 활성 회원
    private long suspendedMembers;   // 정지 회원
    private long deletedMembers;     // 탈퇴 회원 (soft delete 구현 필요)
    private long helpButtonClicks;   // 도우미 버튼 클릭 수
}