package com.take.take_breath.record.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordUpdateRequest {
    private String title;
    private String content;
    // private Timestamp updateDate;

    // 새로 추가할 파일들
    private List<MultipartFile> imageFiles;
    private List<MultipartFile> audioFiles;
    private List<MultipartFile> videoFiles;
}
