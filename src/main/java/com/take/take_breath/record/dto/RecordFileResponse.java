package com.take.take_breath.record.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordFileResponse {
    private String fileName;
    private String originalName;
    private String url;
    private Long size;
    private String contentType;
}
