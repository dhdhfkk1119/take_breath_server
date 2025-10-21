package com.take.take_breath.chat.config;

import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// 웹소켓을 STOMP 프로토콜로 처리 선언
@Configuration  // 설정 클래스
@EnableWebSocketMessageBroker   // 메세지 브로커 기반 사용을 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // WebSocketMessageBrokerConfigurer = 웹소켓 설정을 커스터마이징할 수 있는 인터페이스

    // 클라이언트가 연결할 엔드포인트 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
        registry.addEndpoint("/ws-chat")         // 클라이언트가 WebSocket 연결할 주소 (ws://localhost:8080/ws)
                .setAllowedOriginPatterns("*");      // CORS 설정 - 모든 도메인에서 접근 허용
                // .withSockJS();                      // SockJS 지원 - WebSocket이 지원되지 않는 브라우저에서도 동작하도록 설정
    }

    // 메세지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);

        // 서버에서 클라이언트로 메세지를 전달할 경로 (prefix) 지정
        // 클라이언트는 /topic/... 주소를 구독해야 메세지를 받을 수 있음
        // 서버 -> 클라 메세지 경로 : /topic/...
        registry.enableSimpleBroker("/topic");      // /topic으로 시작하는 모든 경로를 브로커가 처리하겠다는 의미

        // 클라이언트가 서버로 메세지를 보낼 때 붙이는 prefix
        // 클라 -> 서버 메세지 경로 : /app/...
        registry.setApplicationDestinationPrefixes("/app");
    }
}
/**
 * STOMP에서의 흐름
 * 1. 클라이언트가 채널을 구독
 *      stompClient.subscribe("/topic/public", (msg) => {
 *           console.log("받은 메시지:", msg.body);
 *      });
 *      - 클라이언트는 /topic/public 이라는 채널을 듣고 있다는 상태
 *
 * 2. 다른 클라이언트가 메세지를 보냄
 *      stompClient.send("/app/chat.send", {}, "안녕하세요");
 *      - /app/... 은 서버로 가는 경로 (Controller @MessageMapping 으로 연결)
 *      - 서버가 이 메시지를 처리한 뒤 특정 채널로 브로드캐스트
 *
 * 3. 서버가 브로커를 통해 채널에 전달
 *      @MessageMapping("/chat.send")
 *      @SendTo("/topic/public")
 *      public String sendMessage(String message) {
 *          return message;
 *      }
 *      - 서버는 /topic/public 채널에 메시지를 발행
 *
 *
  */
