package com.take.take_breath.chat.chat_room_member;

import com.take.take_breath.chat.chat_room.ChatRoom;
import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_member_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class ChatRoomMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chatRoom;              // 채팅방

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;                  // 채팅방에 속한 멤버

    private Long lastReadMessageId;     // 이 사용자가 이 방에서 마지막으로 읽은 메시지 ID

    private Timestamp lastReadAt;   // 마지막으로 읽은 시각

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Timestamp joinedAt;         // 방에 참여한 시각
    
    // 읽음처리
    public void updateLastRead(Long messageId) {
        this.lastReadMessageId = messageId;
        this.lastReadAt = Timestamp.valueOf(LocalDateTime.now());
    }
}

/*

나중에 이거 지원
@Table(name = "chat_room_member_tb",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"chat_room_id", "member_id"})
       })

 */