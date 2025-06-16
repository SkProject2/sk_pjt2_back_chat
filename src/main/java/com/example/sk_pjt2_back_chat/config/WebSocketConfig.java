package com.example.sk_pjt2_back_chat.config;

import com.example.sk_pjt2_back_chat.interceptor.StompInterceptor;
import com.example.sk_pjt2_back_chat.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // STOMP 연결시 Redis 기록용
    @Autowired
    private UserSessionService userSessionService;

    // STOMP 사용을 위한 Message Broker 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // STOMP Subscribe prefix 설정
        config.enableSimpleBroker("/sub");
        // STOMP Publish prefix 설정
        config.setApplicationDestinationPrefixes("/pub");
    }

    // 소켓 연결 관련 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // sockJS용 엔트포인트 주소 및 CORS 설정
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*")
                .withSockJS();
        // STOMP 프로토콜용 엔트포인트 주소 및 CORS 설정
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new StompInterceptor(userSessionService));
    }
}
