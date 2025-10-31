package com.take.take_breath.chat.chat_room;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RoomType roomType = RoomType.COUNSELING;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Timestamp createdAt;    // 채팅방 생성 시간
}
