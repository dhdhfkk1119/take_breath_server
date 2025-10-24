package com.take.take_breath.chat.chat_room_member;

import com.take.take_breath.chat.chat_room.ChatRoom;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_member_tb")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatRoomMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = true)
    private Long lastReadMessageId;     // 이 사용자가 이 방에서 마지막으로 읽은 메시지 ID

    @Column(nullable = true)
    private LocalDateTime lastReadAt;   // 마지막으로 읽은 시각

    // 방에 참여한 시각
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    public void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }
}

/*

나중에 이거 지원
@Table(name = "chat_room_member_tb",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"chat_room_id", "member_id"})
       })

 */