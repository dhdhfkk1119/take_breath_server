package com.take.take_breath.notification;

import com.take.take_breath._core._utils.SseUtil;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.notification.dto.NotificationRequest;
import com.take.take_breath.notification.dto.NotificationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final SseUtil sseUtil;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final FcmService fcmService;

    private Member findMemberById(String memberIdStr) {
        Long memberId = Long.parseLong(memberIdStr);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + memberIdStr));
    }

    // 커뮤니티 댓글 알림
    @Async
    public void sendComment(String targetMemberId, String postTitle,
                            String commenterMemberId, String commenterMemberName,Long relatedPostId) {

        if (targetMemberId.equals(commenterMemberId)) {
            return;
        }

        try {
            String message = String.format("%s 게시글에 %s 님이 댓글을 남겼습니다.",
                    postTitle, commenterMemberName);

            Member receiver = findMemberById(targetMemberId);
            Member sender = findMemberById(commenterMemberId);

            NotificationRequest.SSE requestDto = NotificationRequest.SSE.builder()
                    .receiver(receiver)
                    .sender(sender)
                    .notificationType(NotificationType.COMMENT) // 댓글 알림 타입
                    .relatedId(relatedPostId) // 게시글 ID
                    .content(message)
                    .build();

            Notification notification = requestDto.toEntity();
            notificationRepository.save(notification);

            log.info("댓글 알림 전송 to: {}, from: {}", targetMemberId, commenterMemberId);
            sseUtil.sendToUser(targetMemberId, "Comment", message); // 앱이 켜저있을 떄
            String fcmToken = receiver.getFcmToken();
            fcmService.sendFcmMessage(fcmToken, "새 댓글", message, relatedPostId, NotificationType.COMMENT.name());


        } catch (Exception e) {
            sseUtil.sendToUser(targetMemberId, "error", "알림 전송 중 오류가 발생했습니다.");
        }
    }

    // 커뮤니티 게시글 좋아요 알림
    // NotificationService.java (수정된 sendPostLike 메서드)

    @Async
    public void sendPostLike(String targetMemberId, String postTitle,
                             String likerMemberId, String likerMemberName,
                             Long relatedPostId) {

        if (targetMemberId.equals(likerMemberId)) {
            return;
        }

        try{
            String messageContent = String.format("%s 님이 %s 게시글에 좋아요를 눌렀습니다.",
                    likerMemberName, postTitle);

            Member receiver = findMemberById(targetMemberId);
            Member sender = findMemberById(likerMemberId);

            // 1. NotificationRequest DTO 생성
            NotificationRequest.SSE requestDto = NotificationRequest.SSE.builder()
                    .receiver(receiver)
                    .sender(sender)
                    .notificationType(NotificationType.POST_LIKE)
                    .relatedId(relatedPostId)
                    .content(messageContent)
                    .build();

            // 2. DTO를 엔티티로 변환하고 DB에 저장
            Notification notification = requestDto.toEntity();
            notificationRepository.save(notification);

            // 3. SSE 전송 (프론트엔드 알림)
            log.info("좋아요 알림 전송 및 저장 to: {}", targetMemberId);
            sseUtil.sendToUser(targetMemberId, "PostLike", messageContent);

            String fcmToken = receiver.getFcmToken();
            fcmService.sendFcmMessage(fcmToken, "새 좋아요", messageContent, relatedPostId, NotificationType.COMMENT.name());

        } catch (Exception e) {
            log.error("좋아요 알림 처리 중 오류 발생: {}", e.getMessage());
            sseUtil.sendToUser(targetMemberId, "error", "알림 전송 중 오류가 발생했습니다.");
        }
    }

    // 사용자의 알림 목록 조회
    public Page<NotificationResponse.ListDTO> getNotifications(Long memberId, Pageable pageable) {
        // 1. 로그인된 사용자를 receiver로 하는 알림 목록을 시간 순(최신순)으로 조회
        Page<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(memberId,pageable);

        // 2. 응답 DTO로 변환
        return notifications.map(NotificationResponse.ListDTO::new);
    }

    // 알림 읽음 처리 (선택적)
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification); // @Transactional이 붙어있으므로 생략 가능하나 명시적으로 호출
                });
    }

}
