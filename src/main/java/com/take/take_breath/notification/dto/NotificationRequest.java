package com.take.take_breath.notification.dto;

import com.take.take_breath.members.Member;
import com.take.take_breath.notification.Notification;
import com.take.take_breath.notification.NotificationType;
import lombok.Builder;
import lombok.Data;

public class NotificationRequest {

    @Data
    @Builder
    public static class SSE{
        private Member receiver;
        private Member sender;
        private NotificationType notificationType;
        private Long relatedId;
        private String content;

        public Notification toEntity() {
            return Notification.builder()
                    .receiver(this.receiver)
                    .sender(this.sender)
                    .notificationType(this.notificationType)
                    .relatedId(this.relatedId)
                    .content(this.content)
                    .isRead(false) // 생성 시 읽음 여부는 항상 false
                    .build();
        }
    }
}
