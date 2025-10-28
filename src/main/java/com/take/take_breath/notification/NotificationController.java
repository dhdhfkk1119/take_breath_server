package com.take.take_breath.notification;


import com.take.take_breath._core._utils.SseUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
// CORS 모두 허용
@CrossOrigin(origins = "*")
public class NotificationController {

    private final SseUtil sseUtil;

    // 구독하기
    @Auth(statuses = {Status.ACTIVE})
    @GetMapping(value = "/subscribe/{userId}", produces = "text/event-stream")
    public SseEmitter subscribe(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        return sseUtil.addEmitter(String.valueOf(memberId));
    }
}
