package com.take.take_breath.community.community_report;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class CommunityReportRequest {

    @Data
    public static class CreateDTO {
        @NotBlank(message = "신고 사유는 필수입니다.")
        private String reason;
    }
}
