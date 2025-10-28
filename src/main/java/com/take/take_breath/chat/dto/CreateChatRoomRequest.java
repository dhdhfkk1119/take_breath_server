package com.take.take_breath.chat.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CreateChatRoomRequest {
    private String roomName;            // 채팅방 이름 (선택사항)
    private List<Long> memberIds;       // 참여할 회원 ID 목록 (1:1이면 2명)
}