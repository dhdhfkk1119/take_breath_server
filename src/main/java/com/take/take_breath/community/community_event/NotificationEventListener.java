package com.take.take_breath.community.community_event;

import com.take.take_breath.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        log.info("[이벤트 처리] 댓글 작성 알림: postUserId={}, postTitle={}",
                event.getPostUserId(), event.getPostTitle());

        notificationService.sendComment(
                event.getPostUserId().toString(),
                event.getPostTitle(),
                event.getCommenterUserId().toString()
        );
    }

    @EventListener
    public void handlePostLike(PostLikeEvent event) {
        log.info("[이벤트 처리] 게시글 좋아요 알림: postUserId={}, postTitle={}",
                event.getPostUserId(), event.getPostTitle());

        notificationService.sendPostLike(
                event.getPostUserId().toString(),
                event.getPostTitle(),
                event.getLikerUserId().toString()
        );
    }
}