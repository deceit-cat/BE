package com.capstone.backend.domain.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /* 클라이언트가 웹 소켓 서버에 연결하는데 사용할 웹 소켓 엔드포인트 등록 */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 클라이언트에 제공하는 엔드포인트
                .setAllowedOrigins("*");
    }

    /* 한 클라이언트에서 다른 클라이언트로 메세지를 라우팅하는데 사용될 메세지 브로커 */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/sub"); // 채팅방 구독
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/pub"); // 메세지 발행
    }}
