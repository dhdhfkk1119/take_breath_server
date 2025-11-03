package com.take.take_breath.record.dto;

import com.take.take_breath.record.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
public class RecordListResponse {
    private Long id;
    private String title;
    private String content;
    private String recordDate;
    private String updatedDate;
    private Integer imageFileCount;
    private Integer audioFileCount;
    private Integer videoFileCount;

    public static RecordListResponse fromEntity(Record record) {
        return RecordListResponse.builder()
                .id(record.getId())
                .title(record.getTitle())
                .content(record.getContent())
                .recordDate(record.getRecordDateTime())
                .updatedDate(record.getUpdatedDate() != null ?
                        record.getUpdateDateTime() : null)
                .imageFileCount(record.getImageFiles().size())
                .audioFileCount(record.getAudioFiles().size())
                .videoFileCount(record.getVideoFiles().size())
                .build();
    }
}
