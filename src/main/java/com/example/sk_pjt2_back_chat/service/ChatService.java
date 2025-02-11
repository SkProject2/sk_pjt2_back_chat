package com.example.sk_pjt2_back_chat.service;

import com.example.sk_pjt2_back_chat.dto.ChatDto;
import com.example.sk_pjt2_back_chat.entity.Chat;
import com.example.sk_pjt2_back_chat.repository.ChatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 채팅 관련 로직을 넣은 서비스
 * STOMP을 이용해 문자 전송 등을 담당할 예정
 * 여기서 Kafka Producer를 사용해 Kafka로 보낸다.
 *   이후 Kafka Consumer가 알아서 Kafka에서 가져올것이므로 신경 X
 */

@Service
@RequiredArgsConstructor
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private final SimpMessageSendingOperations messagingTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Date date = new Date();

    // Kafka Producer
    public void sendMessageWithKafka(Long roomId, ChatDto chatDto) throws JsonProcessingException {
        System.out.println("채팅 서비스 메시지 전송 시작");
        Chat chat = Chat.builder()
                .id(UUID.randomUUID().toString())
                .roomId(roomId)
                .sender(chatDto.getSender())
                .message(chatDto.getContent())
                .timestamp(date.getTime())
                .build();

        System.out.println("메세지 채널 전송 시도: " + roomId);
        kafkaTemplate.send("chat", objectMapper.writeValueAsString(chatDto));

        chatRepository.save(chat);

        System.out.println("Produce Message: " + chatDto.toString());
    }

    // Kafka Consumer
    @KafkaListener(topics = "chat", groupId = "chat")
    public void receiveMessageWithKafka(String message) throws JsonProcessingException {
        ChatDto chatDto = objectMapper.readValue(message, ChatDto.class);
        System.out.println("Consume Message From Kafka: " + chatDto.toString());

        messagingTemplate.convertAndSend("/sub/chat/room/"+chatDto.getRoomId(), chatDto);
        System.out.println("Send Message: " + chatDto.toString());
    }

    public void sendMessage(Long roomId, ChatDto chatDto) {
        // 받은 메세지 DB에 저장
        System.out.println("메세지 채널 전송 시도: " + roomId);
        Chat chat = Chat.builder()
                .roomId(roomId)
                .sender(chatDto.getSender())
                .message(chatDto.getContent())
                .build();
        chatRepository.save(chat);

        // sub에게 메세지 전달
        System.out.println("메세지 전송 시도: " + chatDto.toString());
        messagingTemplate.convertAndSend("/sub/chat/room/"+roomId, chatDto);
    }

    public List<ChatDto> getAllMessageById(Long roomId) {
//        List<Chat> lc = chatRepository.findAllByRoomId(roomId);
        List<Chat> lc = mongoTemplate.find(new Query(new Criteria("roomId").is(roomId)),Chat.class);
        return lc.stream().map(
                Chat::toDto
        ).toList();
    }
}
