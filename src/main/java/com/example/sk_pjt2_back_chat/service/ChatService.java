package com.example.sk_pjt2_back_chat.service;

import com.example.sk_pjt2_back_chat.dto.ChatDto;
import com.example.sk_pjt2_back_chat.entity.Chat;
import com.example.sk_pjt2_back_chat.entity.Room;
import com.example.sk_pjt2_back_chat.repository.ChatRepository;
import com.example.sk_pjt2_back_chat.repository.RoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 채팅 관련 로직을 넣은 서비스
 * STOMP을 이용해 문자 전송 등을 담당할 예정
 * 여기서 Kafka Producer를 사용해 Kafka로 보낸다.
 * 이후 Kafka Consumer가 알아서 Kafka에서 가져올것이므로 신경 X
 */

@Service
@RequiredArgsConstructor
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserSessionService userSessionService;

    private final SimpMessageSendingOperations messagingTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Kafka Producer
    public void sendMessageWithKafka(String roomUUID, ChatDto chatDto) throws JsonProcessingException {
        System.out.println("채팅 서비스 메시지 전송 시작");
        Chat chat = Chat.builder()
                .id(UUID.randomUUID().toString())
                .room(roomRepository.findByRoomUUID(roomUUID))
                .sender(chatDto.getSender())
                .message(chatDto.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        System.out.println("메세지 채널 전송 시도: " + roomUUID);
        kafkaTemplate.send("chat", objectMapper.writeValueAsString(chatDto));

        chatRepository.save(chat);

        System.out.println("Produce Message: " + chatDto.toString());
    }

    // Kafka Consumer
    @KafkaListener(topics = "chat", groupId = "team5-${server.id}") // 서버별 다른 그룹
    public void receiveMessageWithKafka(String message) throws JsonProcessingException {
        ChatDto chatDto = objectMapper.readValue(message, ChatDto.class);

        // 이 서버에 접속한 사용자들에게만 전송
        if (userSessionService.hasUsersInRoom(chatDto.getRoomUUID())) {
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatDto.getRoomUUID(), chatDto);
        }
    }

    public List<ChatDto> getAllMessageById(String roomUUID) {
        Room room = roomRepository.findByRoomUUID(roomUUID);
        List<Chat> lc = chatRepository.findAllByRoomOrderByTimestampAsc(room);
        return lc.stream().map(
                Chat::toDto
        ).toList();
    }
}
