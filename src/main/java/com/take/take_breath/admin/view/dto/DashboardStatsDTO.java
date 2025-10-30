package com.take.take_breath.admin.view.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalMembers;
    private long totalCounselors;
    private long activeCounselors;
    private long pendingCounselors;
}