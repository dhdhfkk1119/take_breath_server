package com.take.take_breath.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateConsultationRequest {
        private Long consultantId;
    }


}
