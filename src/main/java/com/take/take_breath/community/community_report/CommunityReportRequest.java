package com.take.take_breath.community.community_report;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommunityReportRequest {

    @Data
    @NoArgsConstructor
    public static class CreateDTO {
        @NotBlank(message = "신고 사유는 필수입니다.")
        private String reason;

        public CreateDTO(CommunityReport report) {
            this.reason = report.getReason();
        }
    }
}
