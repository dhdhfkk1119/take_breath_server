package com.take.take_breath.chat.config;


import com.take.take_breath._core._exception.Exception401;
import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    private final MemberRepository memberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(accessor != null) {
            StompCommand command = accessor.getCommand();

            if (StompCommand.CONNECT.equals(command)
                    || StompCommand.SUBSCRIBE.equals(command)
                    || StompCommand.SEND.equals(command)) {

                String token = resolveToken(accessor);
                if (token == null || !jwtTokenProvider.validateToken(token)) {
                    log.error("WebSocket JWT 인증 실패: command={}, sessionId={}", command, accessor.getSessionId());
                    throw new Exception401("유효하지 않은 토큰입니다");
                }

                // JWT에서 사용자 정보 추출
                String memberEmail = jwtTokenProvider.getSubject(token);
                Role memberRole = jwtTokenProvider.getRole(token);

                // DB 조회
                Member member = memberRepository.findByEmail(memberEmail)
                        .orElseThrow(() -> new Exception401("유효하지 않은 사용자입니다."));

                // 세션에 저장
                accessor.getSessionAttributes().put("memberEmail", memberEmail);
                accessor.getSessionAttributes().put("memberRole", memberRole);
                accessor.getSessionAttributes().put("memberId", member.getId());

                log.info("WebSocket JWT 인증 성공: email={}, role={}, command={}, sessionId={}",
                        memberEmail, memberRole, command, accessor.getSessionId());
            }
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }

    /**
     * STOMP 헤더에서 JWT 토큰 추출
     */
    private String resolveToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
