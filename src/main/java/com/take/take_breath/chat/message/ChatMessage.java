package com.take.take_breath.chat.message;

import com.take.take_breath.chat.member.Member;
import com.take.take_breath.chat.room.ChatRoom;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message_tb")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // private String attachmentUrl;   // 첨부파일 경로 - 현재는 DB에 넣는 방식 사용

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] attachmentData;  // DB에 데이터를 직접 저장

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;    // 메세지 생성 시간

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}


/*
@Entity
@Table(name = "chat_message_tb")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chat_room_id;

    @Column(nullable = false)
    private String content;
}
*/