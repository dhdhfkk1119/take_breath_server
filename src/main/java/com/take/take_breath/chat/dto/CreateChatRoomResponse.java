package com.take.take_breath.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CreateChatRoomResponse {
    private Long roomId;
    private String roomName;
    private String roomType;
    private LocalDateTime createdAt;
    private List<MemberInfo> members;   // 참여 멤버 목록

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberInfo {
        private Long memberId;
        private String memberName;
        private String email;
    }
}