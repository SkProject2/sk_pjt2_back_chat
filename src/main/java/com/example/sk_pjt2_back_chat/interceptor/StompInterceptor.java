package com.example.sk_pjt2_back_chat.interceptor;

import com.example.sk_pjt2_back_chat.service.UserSessionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.Objects;

public class StompInterceptor implements ChannelInterceptor {

    private final UserSessionService userSessionService;

    public StompInterceptor(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(accessor.getCommand() == StompCommand.CONNECT) {
            String sender = Objects.requireNonNull(accessor.getNativeHeader("sender")).get(0);
            System.out.println("클라이언트 접속: " + sender);
            userSessionService.userConnect(sender);
        }
        if(accessor.getCommand() == StompCommand.SUBSCRIBE) {
            String sub = accessor.getSubscriptionId();
            System.out.println("클라이언트 구독: "+sub);
        }
        if(accessor.getCommand() == StompCommand.DISCONNECT){
            String sender = Objects.requireNonNull(accessor.getNativeHeader("sender")).get(0);
            System.out.println("클라이언트 접속 해제: " + sender);
            userSessionService.userDisconnect(sender);
        }
        return message;
    }
}
