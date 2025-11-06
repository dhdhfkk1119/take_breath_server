package com.take.take_breath.chat.config;

import com.take.take_breath._core._exception.Exception401;
import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtHandshakeHandler extends DefaultHandshakeHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // 1️⃣ HTTP 헤더에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);

        // 2️⃣ 토큰 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getSubject(token);

            // 3️⃣ 사용자 정보 조회
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("유효하지 않은 사용자입니다."));

            // 4️⃣ Principal 생성 (WebSocket 세션에 사용자 식별자 저장)
            return () -> String.valueOf(member.getId());
        }

        // 5️⃣ 인증 실패 시 null 반환 → 연결 거부
        return null;
    }
}
