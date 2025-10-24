package com.take.take_breath.community.community_report_process;

import com.take.take_breath.community.community_report.CommunityReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class CommunityReportProcessRequest {

    @Data
    public static class UpdateStatusDTO {
        @NotNull(message = "신고 처리 상태는 필수입니다.")
        private CommunityReportStatus status;

        private String adminComment;
    }
}