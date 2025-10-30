package com.take.take_breath.record;

import com.take.take_breath._core._utils.DateUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "record_file_tb")
@Getter @NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RecordFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;    // /uploads/record/images/...

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;     // image/jpeg, audio/mpeg 등등

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public String getCreateTime() {
        return DateUtil.chatFormat(createdAt);
    }

    public String getUpdateTime() {
        return DateUtil.chatFormat(updatedAt);
    }
}
/**
 * RecordFile imageFile = RecordFile.builder()
 *     .id(1L)
 *     .record(someRecord)  // Record 엔티티 객체
 *     .fileType(FileType.IMAGE)
 *     .fileName("20251029_a3f2b1c4-5d6e-7f8g-9h0i-1j2k3l4m5n6o.jpg")
 *     .originalFileName("내가_찍은_사진.jpg")
 *     .filePath("/uploads/record/images/20251029_a3f2b1c4-5d6e-7f8g-9h0i-1j2k3l4m5n6o.jpg")
 *     .fileSize(2457600L)  // 2.4MB (바이트 단위)
 *     .contentType("image/jpeg")
 *     .createdAt(Timestamp.valueOf("2025-10-29 14:30:00"))
 *     .updatedAt(Timestamp.valueOf("2025-10-29 14:30:00"))
 *     .build();
 */