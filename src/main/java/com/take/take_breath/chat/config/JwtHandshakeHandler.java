package com.take.take_breath.chat.config;

import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

// 웹소켓 최초 연결 시점에서 한번만 실행됨
@Slf4j
@RequiredArgsConstructor
public class JwtHandshakeHandler extends DefaultHandshakeHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getSubject(token);
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("유효하지 않은 사용자입니다."));

            // 세션에 사용자 정보 저장
            attributes.put("authenticated", true);
            attributes.put("memberId", member.getId());
            attributes.put("memberEmail", member.getEmail());
            attributes.put("memberRole", member.getRole().name());

            // 세션 객체 반환, stomp 컨트롤러에서 파라미터로 주입 가능
            return () -> String.valueOf(member.getId());
        }
        return null;
    }
}
