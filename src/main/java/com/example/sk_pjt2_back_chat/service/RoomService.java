package com.example.sk_pjt2_back_chat.service;

import com.example.sk_pjt2_back_chat.dto.RoomDto;
import com.example.sk_pjt2_back_chat.entity.Room;
import com.example.sk_pjt2_back_chat.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoomRepository roomRepository;
    
    // 유저 삭제시 관련 채팅방 삭제
    @KafkaListener(topics = "user-delete", groupId = "team5")
    public void deleteRoomWithKafka(String email) {
        System.out.println(email + " 유저 삭제로 인한 채팅방 삭제");
        roomRepository.deleteRoomsByUser(email);
        System.out.println("삭제완료");
    }

    public List<RoomDto> findAllRoom(){
        List<Room> lr = roomRepository.findAll();
        if(lr.isEmpty()){
            return null;
        }
        return lr.stream()
                .map(Room::toDto).collect(Collectors.toList());
    }

    public RoomDto findRoomByIdAndUser(String roomUUID, String user){
        Optional<Room> rm = roomRepository.findByRoomUUIDAndUser(roomUUID, user);
        return rm.map(Room::toDto).orElse(null);
    }


    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public RoomDto createRoom(String user, String roomUUID) {
        Room room = Room.builder()
                .roomUUID(roomUUID)
                .user(user)
                .build();
        roomRepository.save(room);
        return room.toDto();
    }
}
