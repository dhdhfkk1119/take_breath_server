package com.take.take_breath.record;

import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "record_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 200, nullable = false)
    private String title;       // 기록 제목

    @Column(columnDefinition = "TEXT")
    private String content;     // 본문 내용

    @CreationTimestamp
    @Column(name = "record_date", updatable = false)
    private Timestamp recordDate; // 기록 날짜

    @UpdateTimestamp
    @Column(name = "update_date")
    private Timestamp updatedDate; // 수정 날짜

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecordFile> recordFiles  = new ArrayList<>();

    public String getTime() {
        return DateUtil.chatFormat(recordDate);
    }

    public List<RecordFile> getImageFiles() {
        return recordFiles.stream()
                .filter(f -> f.getFileType() == FileType.IMAGE)
                .toList();
    }

    public List<RecordFile> getAudioFiles() {
        return recordFiles.stream()
                .filter(f -> f.getFileType() == FileType.AUDIO)
                .toList();
    }

    public List<RecordFile> getVideoFiles() {
        return recordFiles.stream()
                .filter(f -> f.getFileType() == FileType.VIDEO)
                .toList();
    }

    public void addRecordFile(RecordFile recordFile) {
        this.recordFiles.add(recordFile);
    }
}
