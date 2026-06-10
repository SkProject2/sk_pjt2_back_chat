package com.example.sk_pjt2_back_chat.subscriber;

import com.example.sk_pjt2_back_chat.dto.ChatDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RedisChatSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    public RedisChatSubscriber(ObjectMapper objectMapper,
                               @Lazy SimpMessageSendingOperations messagingTemplate) {
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
    }

    // Pub된 Redis 메시지를 받아서 STOMP로 내보내기
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            ChatDto dto = objectMapper.readValue(body, ChatDto.class);
            // 이 서버에서 해당 방을 구독 중인 클라이언트에게만 STOMP 전달
            messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getRoomUUID(), dto);
        } catch (Exception e) {
            System.err.println("Redis 메시지 처리 오류: " + e.getMessage());
        }
    }
}