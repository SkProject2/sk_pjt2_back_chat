// StompInterceptor.java 수정
package com.example.sk_pjt2_back_chat.interceptor;

import com.example.sk_pjt2_back_chat.service.RoomSubscriptionManager;
import com.example.sk_pjt2_back_chat.service.UserSessionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StompInterceptor implements ChannelInterceptor {

    private final UserSessionService userSessionService;
    private final RoomSubscriptionManager roomSubscriptionManager;

    public StompInterceptor(UserSessionService userSessionService,
                            RoomSubscriptionManager roomSubscriptionManager) {
        this.userSessionService = userSessionService;
        this.roomSubscriptionManager = roomSubscriptionManager;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        if(accessor.getCommand() == StompCommand.CONNECT) {
            String sender = getSenderFromHeader(accessor);
            if (sender != null && sessionId != null) {
                System.out.println("클라이언트 접속: " + sender + " (세션: " + sessionId + ")");
                // 사용자 ID와 세션 ID를 모두 저장
                userSessionService.userConnect(sender, sessionId);
            }
        }

//        if(accessor.getCommand() == StompCommand.SUBSCRIBE) {
//            String sub = accessor.getSubscriptionId();
//            System.out.println("클라이언트 구독: " + sub);
//        }
        if(accessor.getCommand() == StompCommand.SUBSCRIBE) {
            String dest = accessor.getDestination();
            String subscriptionId = accessor.getSubscriptionId();
            if(dest != null && dest.startsWith("/sub/chat/room/") && sessionId != null) {
                String roomUUID = dest.substring("/sub/chat/room/".length());
                roomSubscriptionManager.addSubscription(sessionId, subscriptionId, roomUUID);
            }
        }

        if(accessor.getCommand() == StompCommand.UNSUBSCRIBE) {
            String subscriptionId = accessor.getSubscriptionId();
            if(sessionId != null &&  subscriptionId != null) {
                roomSubscriptionManager.removeSubscription(sessionId, subscriptionId);
            }
        }

        if(accessor.getCommand() == StompCommand.DISCONNECT) {
            // sender 헤더 시도
            String sender = getSenderFromHeader(accessor);

            if (sender != null) {
                System.out.println("클라이언트 접속 해제 (sender): " + sender);
                userSessionService.userDisconnect(sender);
            } else if (sessionId != null) {
                // sender가 없으면 세션 ID로 처리
                System.out.println("클라이언트 접속 해제 (세션 ID): " + sessionId);
                userSessionService.disconnectBySessionId(sessionId);
            } else {
                System.out.println("DISCONNECT: sender와 세션 ID 모두 없음");
            }

            if(sessionId != null) {
                roomSubscriptionManager.removeSession(sessionId);
            }
        }

        return message;
    }

    private String getSenderFromHeader(StompHeaderAccessor accessor) {
        try {
            List<String> senderHeaders = accessor.getNativeHeader("sender");
            if (senderHeaders != null && !senderHeaders.isEmpty()) {
                return senderHeaders.get(0);
            }
        } catch (Exception e) {
            System.out.println("sender 헤더 추출 중 오류: " + e.getMessage());
        }
        return null;
    }
}