package com.take.take_breath.point;

import com.take.take_breath._core._utils.DateUtil;
import lombok.Builder;
import lombok.Data;

public class PointResponse {
    
    // 포인트 잔액 조회
    @Data
    @Builder
    public static class BalanceDTO {
        private Long memberId;
        private String memberName;
        private Long point;
    }
    
    // 포인트 히스토리
    @Data
    public static class HistoryDTO {
        private Long id;
        private String type;
        private Long amount;
        private Long balanceAfter;
        private String description;
        private String relatedMemberName;
        private String createdAt;
        
        public HistoryDTO(PointHistory history) {
            this.id = history.getId();
            this.type = history.getType().name();
            this.amount = history.getAmount();
            this.balanceAfter = history.getBalanceAfter();
            this.description = history.getDescription();
            this.relatedMemberName = history.getRelatedMember() != null 
                ? history.getRelatedMember().getName() : null;
            this.createdAt = DateUtil.timestampFormat(history.getCreatedAt());
        }
    }
}