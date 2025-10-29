package com.take.take_breath.record.dto;

import com.take.take_breath.record.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class RecordResponse {
    private Long id;
    private String title;
    private String content;
    private Timestamp recordDate;
    private Timestamp updateDate;
    private List<RecordFileResponse> imageFiles;
    private List<RecordFileResponse> audioFiles;
    private List<RecordFileResponse> videoFiles;

    public static RecordResponse fromEntity(Record record) {
        return RecordResponse.builder()
                .id(record.getId())
                .title(record.getTitle())
                .content(record.getContent())
                .recordDate(record.getRecordDate())
                .updateDate(record.getUpdatedDate())
                .imageFiles(record.getImageFiles().stream()
                        .map(f -> RecordFileResponse.builder()
                                .fileName(f.getFileName())
                                .originalName(f.getOriginalFileName())
                                .url(f.getFilePath())
                                .size(f.getFileSize())
                                .contentType(f.getContentType())
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .audioFiles(record.getAudioFiles().stream()
                        .map(f -> RecordFileResponse.builder()
                                .fileName(f.getFileName())
                                .originalName(f.getOriginalFileName())
                                .url(f.getFilePath())
                                .size(f.getFileSize())
                                .contentType(f.getContentType())
                                .build()
                        ).collect(Collectors.toList())
                )
                .videoFiles(record.getVideoFiles().stream()
                        .map(f -> RecordFileResponse.builder()
                                .fileName(f.getFileName())
                                .originalName(f.getOriginalFileName())
                                .url(f.getFilePath())
                                .size(f.getFileSize())
                                .contentType(f.getContentType())
                                .build()
                        ).collect(Collectors.toList())
                )
                .build();
    }
}
