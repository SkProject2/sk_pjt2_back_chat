package com.example.sk_pjt2_back_chat.service;

import com.example.sk_pjt2_back_chat.dto.RoomDto;
import com.example.sk_pjt2_back_chat.dto.RoomUserDto;
import com.example.sk_pjt2_back_chat.entity.Room;
import com.example.sk_pjt2_back_chat.entity.RoomUser;
import com.example.sk_pjt2_back_chat.repository.RoomRepository;
import com.example.sk_pjt2_back_chat.repository.RoomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomUserService roomUserService;

    // 방제를 받아와서 방을 생성하는 메소드
    public List<RoomUserDto> createRoomWithName(String user1, String user2, String productName){
        Room room = Room.builder()
                .roomUUID(UUID.randomUUID().toString())
                .productName(productName)
                .build();
        roomRepository.save(room);
        List<RoomUserDto> lr = new ArrayList<>();
        lr.add(roomUserService.createRoom(user1, room, user2));
        lr.add(roomUserService.createRoom(user2, room, user1));
        return lr;
    }

    // 방제 없을 때 사용하는 메소드
    public List<RoomUserDto> createRoom(String user1, String user2){
        Room room = Room.builder()
                .roomUUID(UUID.randomUUID().toString())
                .productName(user1 + "_" + user2)
                .build();
        roomRepository.save(room);
        List<RoomUserDto> lr = new ArrayList<>();
        lr.add(roomUserService.createRoom(user1, room, user2));
        lr.add(roomUserService.createRoom(user2, room, user1));
        return lr;
    }

    public String deleteRoom(String roomUUID) {
        roomRepository.deleteByRoomUUID(roomUUID);
        return "success";
    }
}
