package com.take.take_breath.notification;


import com.take.take_breath._core._utils.ApiUtil;
import com.take.take_breath._core._utils.PageUtil;
import com.take.take_breath._core._utils.SseUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Status;
import com.take.take_breath.notification.dto.NotificationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
// CORS 모두 허용
@CrossOrigin(origins = "*")
public class NotificationController {

    private final SseUtil sseUtil;
    private final NotificationService notificationService;

    // 구독하기
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");

        if (memberId == null) {
            // 안전하게 예외 처리
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "토큰이 없거나 유효하지 않습니다."
            );
        }

        return sseUtil.addEmitter(String.valueOf(memberId));
    }

    @Auth(statuses = {Status.ACTIVE})
    @GetMapping("/list")
    public ResponseEntity<?> getNotificationList(HttpServletRequest request,
                                              @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long memberId = (Long) request.getAttribute("memberId");

        if (memberId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "사용자 인증 정보가 없습니다."
            );
        }

        Page<NotificationResponse.ListDTO> notifications = notificationService.getNotifications(memberId,pageable);
        return ResponseEntity.ok(ApiUtil.success(PageUtil.PageResponse.of(notifications)));
    }

    // 알림 읽음 처리 API (선택적)
    @Auth(statuses = {Status.ACTIVE})
    @PostMapping("/read/{id}")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
