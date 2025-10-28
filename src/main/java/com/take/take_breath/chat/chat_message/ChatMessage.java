package com.take.take_breath.chat.chat_message;

import com.take.take_breath._core._utils.DateUtil;
import com.take.take_breath.chat.chat_room.ChatRoom;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.unit.DataUnit;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;     // 메시지 본문

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member sender;      // 보낸 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;  // 채팅방

    @Enumerated(EnumType.STRING)
    private MessageType type = MessageType.TEXT;

    @Enumerated(EnumType.STRING)
    private MessageStatus status = MessageStatus.SENT;

    // 파일 저장 경로 (상대 경로)
    // 예: "2025/10/27/a3f5b2c1-4d8e-4f1a-9c3b-1e5f6a7b8c9d.jpg"
    private String attachmentPath;

    // 원본 파일명 (선택사항)
    // 예: "강아지사진.jpg"
    private String originalFilename;

    // 파일 크기 (byte 단위)
    private Long fileSize;

    @CreationTimestamp
    private Timestamp createdAt;    // 메세지 생성 시간

    public String getTime(){
        return DateUtil.chatFormat(createdAt);
    }

    // 이미지 URL 생성
    public String getImageUrl() {
        if (this.type == MessageType.IMAGE && this.id != null) {
            return "/api/chat/messages/image/" + this.id;
        }
        return null;
    }
}