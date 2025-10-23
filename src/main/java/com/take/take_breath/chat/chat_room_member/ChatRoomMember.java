package com.take.take_breath.chat.chat_room_member;

import com.take.take_breath.chat.member.Member;
import com.take.take_breath.chat.message.ChatMessage;
import com.take.take_breath.chat.room.ChatRoom;
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

    // 이 사용자가 이 방에서 마지막으로 읽은 메시지 ID
    private Long lastReadMessageId;

    // 마지막으로 읽은 시각
    private LocalDateTime lastReadAt;

    // 방에 참여한 시각
    @Column(updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    public void onJoin() {
        this.joinedAt = LocalDateTime.now();
    }
}