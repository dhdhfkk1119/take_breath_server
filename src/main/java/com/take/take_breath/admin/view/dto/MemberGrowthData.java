package com.take.take_breath.admin.view.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberGrowthData {
    private List<String> labels;  // 월 레이블 (예: "2024년 6월")
    private List<Integer> memberData;   // 해당 월의 총 일반회원수
    private List<Integer> counselorData;   // 해당 월의 총 상담사 수
}




