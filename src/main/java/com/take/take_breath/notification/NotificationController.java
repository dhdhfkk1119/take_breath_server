package com.take.take_breath.notification;


import com.take.take_breath._core._utils.SseUtil;
import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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


}
