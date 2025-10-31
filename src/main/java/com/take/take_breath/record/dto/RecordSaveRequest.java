package com.take.take_breath.record.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordSaveRequest {
    private String title;
    private String content;
    // private Timestamp recordDate;
    private List<MultipartFile> imageFiles;
    private List<MultipartFile> audioFiles;
    private List<MultipartFile> videoFiles;
}
