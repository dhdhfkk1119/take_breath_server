package com.take.take_breath.record.dto;

import com.take.take_breath.record.Record;
import com.take.take_breath.record.RecordFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RecordResponse {
    private Long id;
    private String title;
    private String content;
    private String recordDate;
    private String updatedDate;
    private MemberInfo member;
    private List<FileInfo> imageFiles;
    private List<FileInfo> audioFiles;
    private List<FileInfo> videoFiles;
    private Integer totalFileCount;

    public static RecordResponse fromEntity(Record record) {
        List<FileInfo> images = record.getImageFiles().stream()
                .map(FileInfo::from)
                .toList();

        List<FileInfo> audios = record.getAudioFiles().stream()
                .map(FileInfo::from)
                .toList();

        List<FileInfo> videos = record.getVideoFiles().stream()
                .map(FileInfo::from)
                .toList();

        return RecordResponse.builder()
                .id(record.getId())
                .title(record.getTitle())
                .content(record.getContent())
                .recordDate(record.getRecordDateTime())
                .updatedDate(record.getUpdatedDate() != null ?
                        record.getUpdateDateTime() : null)
                .member(MemberInfo.from(record.getMember()))
                .imageFiles(images)
                .audioFiles(audios)
                .videoFiles(videos)
                .totalFileCount(images.size() + audios.size() + videos.size())
                .build();
    }

    @Getter
    @Builder
    public static class MemberInfo {
        private Long id;
        private String name;
        private String email;

        public static MemberInfo from(com.take.take_breath.members.Member member) {
            return MemberInfo.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FileInfo {
        private Long id;
        private String fileType;
        private String originalFileName;
        private String filePath;
        private Long fileSize;
        private String contentType;
        private String createdAt;
        private String updatedAt;

        public static FileInfo from(RecordFile recordFile) {
            return FileInfo.builder()
                    .id(recordFile.getId())
                    .fileType(recordFile.getFileType().name())
                    .originalFileName(recordFile.getOriginalFileName())
                    .filePath(recordFile.getFilePath())
                    .fileSize(recordFile.getFileSize())
                    .contentType(recordFile.getContentType())
                    .createdAt(recordFile.getCreateTime())
                    .updatedAt(recordFile.getUpdateTime())
                    .build();
        }
    }
}
