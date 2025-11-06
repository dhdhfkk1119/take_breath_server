package com.take.take_breath.chat.config;


import com.take.take_breath._core._jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * 웹소켓 stomp 메세지에 대한 JWT 인증 인터셉터
 * 동작 시점:
 * - CONNECT: WebSocket 연결 시
 * - SUBSCRIBE: 토픽 구독 시
 * - SEND: 메시지 전송 시
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())
                || StompCommand.SUBSCRIBE.equals(accessor.getCommand())
                || StompCommand.SEND.equals(accessor.getCommand())) {

            String token = jwtTokenProvider.resolveToken(accessor);

            if (token == null || !jwtTokenProvider.validateToken(token)) {
                log.warn("❌ 잘못된 토큰: 메시지 차단됨");
                throw new IllegalArgumentException("유효하지 않은 토큰");
            }
        }
        return message;
    }
}

/*
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();

            // CONNECT, SUBSCRIBE, SEND 명령 시 토큰 검증
            if (StompCommand.CONNECT.equals(command)
                    || StompCommand.SUBSCRIBE.equals(command)
                    || StompCommand.SEND.equals(command)) {

                // JWT 토큰 추출
                String token = resolveToken(accessor);

                if (token != null && jwtTokenProvider.validateToken(token)) {
                    String email = jwtTokenProvider.getSubject(token);
                    memberRepository.findByEmail(email).ifPresent(member -> {
                        accessor.getSessionAttributes().put("memberId", member.getId());
                        accessor.getSessionAttributes().put("memberEmail", member.getEmail());
                    });

                    log.info("✅ WebSocket 인증 성공: {}", email);
                } else {
                    log.warn("❌ WebSocket 인증 실패 - 잘못된 토큰 또는 누락됨");
                    throw new IllegalArgumentException("Invalid JWT token in WebSocket message");
                }
            }
        }

        return message; // 메시지를 계속 흐르게 함
    }

    // STOMP 헤더에서 JWT 토큰 추출
    private String resolveToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
*/
