package com.take.take_breath.chat.message;

import com.take.take_breath.chat.member.Member;
import com.take.take_breath.chat.room.ChatRoom;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "chat_message_tb")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 기본 키

    private String sender;      // 보낸 사람

    private String content;     // 메시지 본문

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    public ChatMessage(String sender, String content, ChatRoom chatRoom) {
        this.sender = sender;
        this.content = content;
        this.chatRoom = chatRoom;
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