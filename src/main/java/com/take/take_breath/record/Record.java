package com.take.take_breath.record;

import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.chat.chat_message.MessageType;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "record_tb")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Record {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 200, nullable = false)
    private String title;       // 기록 제목

    @Column(columnDefinition = "TEXT")
    private String content;     // 본문 내용

    @Column(name = "record_date", nullable = false)
    private Timestamp recordDate; // 기록 날짜

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecordFile> imageFileList = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecordFile> audioFileList = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecordFile> videoFileList = new ArrayList<>();

    public String getTime(){
        return DateUtil.chatFormat(recordDate);
    }
}
