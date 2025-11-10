package com.take.take_breath.chat.chat_message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.chat.chat_room.ChatRoom;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;

@Entity
@Table(name = "chat_message_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;     // 메시지 본문

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member sender;      // 보낸 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chatRoom;  // 채팅방

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageType type = MessageType.TEXT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;

    // 파일 저장 경로 (상대 경로) - "2025/10/27/a3f5b2c1-4d8e-4f1a-9c3b-1e5f6a7b8c9d.jpg"
    @Column(name = "attachment_path")
    private String attachmentPath;

    // 원본 파일명 (선택사항) - "강아지사진.jpg"
    @Column(name = "original_filename")
    private String originalFilename;

    // 파일 크기 (byte 단위)
    @Column(name = "file_size")
    private Long fileSize;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp createdAt;    // 메세지 생성 시간

    public String getTime() {
        return DateUtil.chatFormat(createdAt);
    }
}