package com.example.sk_pjt2_back_chat.controller;

import com.example.sk_pjt2_back_chat.dto.RoomDto;
import com.example.sk_pjt2_back_chat.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomService roomService;

    // 채팅방 생성
    @PostMapping("/create")
    public RoomDto createRoom(@RequestParam("seller") String owner,
                              @RequestHeader("user") String buyer) {
        return roomService.createRoom(owner, buyer);
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
