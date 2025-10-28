package com.take.take_breath.notification;

import com.take.take_breath._core._utils.SseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SseUtil sseUtil;

    // 커뮤니티 댓글 알림
    @Async
    public void sendComment(String targetMemberId, String postTitle,
                            String commenterMemberId, String commenterMemberName) {

        if (targetMemberId.equals(commenterMemberId)) {
            return;
        }

        try {
            String message = String.format("%s 게시글에 %s 님이 댓글을 남겼습니다.",
                    postTitle, commenterMemberName);
            log.info("댓글 알림 전송 to: {}, from: {}", targetMemberId, commenterMemberId);
            sseUtil.sendToUser(targetMemberId, "Comment", message);
        } catch (Exception e) {
            sseUtil.sendToUser(targetMemberId, "error", "알림 전송 중 오류가 발생했습니다.");
        }
    }

    // 커뮤니티 게시글 좋아요 알림
    @Async
    public void sendPostLike(String targetMemberId, String postTitle,
                             String likerMemberId, String likerMemberName) {

        if (targetMemberId.equals(likerMemberId)) {
            return;
        }

        try{
            String message = String.format("%s 님이 %s 게시글에 좋아요를 눌렀습니다.",
                    likerMemberName, postTitle);
            log.info("좋아요 알림 전송 to: {}, from: {}", targetMemberId, likerMemberId);
            sseUtil.sendToUser(targetMemberId, "PostLike", message);
        } catch (Exception e) {
            sseUtil.sendToUser(targetMemberId, "error", "알림 전송 중 오류가 발생했습니다.");
        }
    }

}
