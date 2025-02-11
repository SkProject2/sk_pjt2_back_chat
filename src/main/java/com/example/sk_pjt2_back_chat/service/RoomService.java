package com.example.sk_pjt2_back_chat.service;

import com.example.sk_pjt2_back_chat.dto.RoomDto;
import com.example.sk_pjt2_back_chat.entity.Room;
import com.example.sk_pjt2_back_chat.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<RoomDto> findAllRoom(){
        List<Room> lr = roomRepository.findAll();
        if(lr.isEmpty()){
            return null;
        }
        return lr.stream()
                .map(Room::toDto).collect(Collectors.toList());
    }

    public RoomDto findRoomById(Long id){
        Optional<Room> room = roomRepository.findById(id);
        return room.map(Room::toDto).orElse(null);
    }


    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public RoomDto createRoom(String seller, String buyer) {
        String roomName = UUID.randomUUID().toString();
        Room room = Room.builder()
                .name(roomName)
                .seller_id(Long.parseLong(seller))
                .buyer_id(Long.parseLong(buyer))
                .build();
        roomRepository.save(room);

        room = roomRepository.findByName(roomName).orElse(null);
        if (room != null) {
            return room.toDto();
        }
        else return null;
    }
}
