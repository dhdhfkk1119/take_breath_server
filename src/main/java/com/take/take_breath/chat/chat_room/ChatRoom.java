package com.take.take_breath.chat.chat_room;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_tb")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType = RoomType.PRIVATE;   // 채팅방 유형 (1:1 or 그룹)

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;    // 채팅방 생성 시간

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
