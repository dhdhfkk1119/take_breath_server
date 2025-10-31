package com.take.take_breath.record.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UploadedFileInfo {
    private String originalFileName;  // 원본 파일명
    private String savedFileName;     // 저장된 파일명 (유니크명)
    private String filePath;          // DB 저장용 상대 경로
    private Long fileSize;            // 파일 크기
    private String contentType;       // 컨텐츠 타입
}
