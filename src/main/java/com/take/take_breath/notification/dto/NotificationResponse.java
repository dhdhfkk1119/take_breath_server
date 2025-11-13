package com.take.take_breath.notification.dto;

import com.take.take_breath.notification.Notification;
import com.take.take_breath.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

public class NotificationResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListDTO{
        private Long id;
        private String content;
        private String senderName;
        private String receiverName;
        private NotificationType notificationType;
        private Long relatedId; // 게시글 ID, 댓글 ID 등
        private boolean isRead;
        private String profileImageUrl;
        private Timestamp createdAt;

        public ListDTO(Notification notification) {
            this.id = notification.getId();
            this.content = notification.getContent();
            this.senderName = notification.getSender().getNickName();
            this.receiverName = notification.getReceiver().getNickName();
            this.notificationType = notification.getNotificationType();
            this.relatedId = notification.getRelatedId();
            this.profileImageUrl = notification.getSender() != null
                    ? notification.getSender().getProfileImage()
                    : null;
            this.isRead = notification.isRead();
            this.createdAt = notification.getCreatedAt();
        }

    }
}
