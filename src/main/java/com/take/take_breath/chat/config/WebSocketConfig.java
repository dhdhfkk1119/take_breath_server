package com.take.take_breath.chat.config;

import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.members.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtChannelInterceptor jwtChannelInterceptor;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    /**
     * 웹소켓 연결을 위한 엔드포인트 등록
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new JwtHandshakeHandler(jwtTokenProvider, memberRepository))
                .withSockJS();

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new JwtHandshakeHandler(jwtTokenProvider, memberRepository));
    }

    /**
     * 클라이언트로부터 들어오는 메시지를 처리하는 채널을 설정
     *
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }

    /**
     * 메세지 브로커 설정
     *
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");     // 클라 -> 서버 - [전송]
        registry.enableSimpleBroker("/sub");  // 서버 -> 클라 - [구독]
    }

    /**
     * 웹소켓 전송 관련 설정 (메시지 크기 제한, 버퍼 크기 등)을 구성
     *
     * @param registration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(1024 * 1024);        // 한 번에 받을 수 있는 메시지 크기의 최대치 → 여기선 1MB
        registration.setSendBufferSizeLimit(1024 * 1024 * 2); // 송신 버퍼 크기
        registration.setSendTimeLimit(20 * 1000);             // 송신 제한 시간 (밀리초)
    }

}

// 연결 - ws://localhost:8080/ws-chat
// 구독 - /topic/public
// 메세지 전송 - /app/chat.sendMessage