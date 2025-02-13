package com.example.sk_pjt2_back_chat.controller;

import com.example.sk_pjt2_back_chat.dto.RoomDto;
import com.example.sk_pjt2_back_chat.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomService roomService;

    // 채팅방 생성
    @PostMapping("/create")
    public List<RoomDto> createRoom(@RequestParam("user") String user1,
                              @RequestHeader("user") String user2) {
        List<RoomDto> roomDtos = new ArrayList<>();
        String uuid = UUID.randomUUID().toString();
        roomDtos.add( roomService.createRoom(user1, uuid));
        roomDtos.add( roomService.createRoom(user2, uuid));
        return roomDtos;

    }

    // 모든 채팅방 조회
    @GetMapping("/rooms")
    public List<RoomDto> getRooms() {
        return roomService.findAllRoom();
    }

    // 특정 채팅방 조회
    @GetMapping("/{roomUUID}")
    public RoomDto getRoom(@PathVariable("roomUUID") String roomId,
                           @RequestHeader("x-user") String user) {
        return roomService.findRoomByIdAndUser(roomId, user);
    }

    // 채팅방 입장
    @GetMapping("/enter/{roomUUID}")
    public RoomDto enterRoom(@PathVariable("roomUUID") String roomId,
                             @RequestHeader("x-user") String user) {
        // 원래 HTML이나 정보를 줘서 클라이언트단에서 조회하도록 구성해야함
        return roomService.findRoomByIdAndUser(roomId, user);
    }



}
