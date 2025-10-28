package com.take.take_breath.record;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "record_file_tb")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecordFile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;    // IMAGE, AUDIO, VIDEO

    @Column(name = "file_name", nullable = false)
    private String fileName;    // 날짜_UUID.자료형

    @Column(name = "original_file_name")
    private String originalFileName;    // 클라이언트가 올린 원본 파일명

    @Column(name = "file_path", nullable = false)
    private String filePath;    // /upload/record/images/...

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;     // image/jpeg, audio/mpeg 등등
}
