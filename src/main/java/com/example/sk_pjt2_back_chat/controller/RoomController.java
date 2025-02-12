package com.example.sk_pjt2_back_chat.controller;

import com.example.sk_pjt2_back_chat.dto.RoomDto;
import com.example.sk_pjt2_back_chat.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
        roomDtos.add( roomService.createRoom(user1));
        roomDtos.add( roomService.createRoom(user2));
        return roomDtos;

    }

    // 모든 채팅방 조회
    @GetMapping("/rooms")
    public List<RoomDto> getRooms() {
        return roomService.findAllRoom();
    }

    // 특정 채팅방 조회
    @GetMapping("/{roomId}")
    public RoomDto getRoom(@PathVariable("roomId") Long roomId) {
        return roomService.findRoomById(roomId);
    }

    // 채팅방 입장
    @GetMapping("/enter/{roomId}")
    public RoomDto enterRoom(@PathVariable("roomId") Long roomId) {
        // 원래 HTML이나 정보를 줘서 클라이언트단에서 조회하도록 구성해야함
        return roomService.findRoomById(roomId);
    }



}
