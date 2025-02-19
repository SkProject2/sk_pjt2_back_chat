package com.example.sk_pjt2_back_chat.service;

import com.example.sk_pjt2_back_chat.dto.RoomUserDto;
import com.example.sk_pjt2_back_chat.entity.Room;
import com.example.sk_pjt2_back_chat.entity.RoomUser;
import com.example.sk_pjt2_back_chat.repository.RoomRepository;
import com.example.sk_pjt2_back_chat.repository.RoomUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomUserService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoomUserRepository roomUserRepository;
    @Autowired
    private RoomRepository roomRepository;
    
    // 유저 삭제시 관련 채팅방 삭제
    @KafkaListener(topics = "user-delete", groupId = "team5")
    public void deleteRoomWithKafka(String email) {
        System.out.println(email + " 유저 삭제로 인한 채팅방 삭제");
        roomUserRepository.deleteRoomsByUser(email);
        System.out.println("삭제완료");
    }

    public List<RoomUserDto> findAllRoom(){
        List<RoomUser> lr = roomUserRepository.findAll();
        return lr.stream()
                .map(RoomUser::toDto).collect(Collectors.toList());
    }

    public RoomUserDto findRoomByIdAndUser(String roomUUID, String user){
        Room room = roomRepository.findByRoomUUID(roomUUID);
        Optional<RoomUser> rm = roomUserRepository.findByRoomAndUser(room, user);
        return rm.map(RoomUser::toDto).orElse(null);
    }


    //  STOMP 사용으로 미사용 추측
//    public <T> void sendMessage(WebSocketSession session, T message) {
//        try{
//            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public RoomUserDto createRoom(String user, Room room, String other) {
        RoomUser roomUser = RoomUser.builder()
                .room(room)
                .user(user)
                .other(other)
                .build();
        roomUserRepository.save(roomUser);
        return roomUser.toDto();
    }

    public List<RoomUserDto> findAllRoomByUser(String user){
        List<RoomUser> lr = roomUserRepository.findAllByUser(user);
        return lr.stream().map(RoomUser::toDto).collect(Collectors.toList());
    }
}
