package com.example.sk_pjt2_back_chat.controller;

import com.example.sk_pjt2_back_chat.dto.RoomUserDto;
import com.example.sk_pjt2_back_chat.service.RoomService;
import com.example.sk_pjt2_back_chat.service.RoomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomUserService roomUserService;
    @Autowired
    private RoomService roomService;

    // 채팅방 생성
    @PostMapping("/create")
    public List<RoomUserDto> createRoom(@RequestParam("user") String user1,
                                        @RequestHeader("X-Auth-User") String user2) {
        return roomService.createRoom(user1, user2);
    }

    // 모든 채팅방 조회
    @GetMapping("/rooms")
    public List<RoomUserDto> getRooms() {
        return roomUserService.findAllRoom();
    }

    // 특정 채팅방 조회
    @GetMapping("/{roomUUID}")
    public RoomUserDto getRoom(@PathVariable("roomUUID") String roomId,
                               @RequestHeader("X-Auth-User") String user) {
        return roomUserService.findRoomByIdAndUser(roomId, user);
    }

    // 채팅방 입장
    @GetMapping("/enter/{roomUUID}")
    public RoomUserDto enterRoom(@PathVariable("roomUUID") String roomId,
                                 @RequestHeader("X-Auth-User") String user) {
        // 원래 HTML이나 정보를 줘서 클라이언트단에서 조회하도록 구성해야함
        return roomUserService.findRoomByIdAndUser(roomId, user);
    }

    // 사용자가 참여하고 있는 모든 채팅방 조회
    @GetMapping("/list")
    public List<RoomUserDto> getRooms(@RequestHeader("X-Auth-User") String user) {
        return roomUserService.findAllRoomByUser(user);
    }

    // CASCADE 설정 후 사용할 예정
    @DeleteMapping("/delete/{roomUUID}")
    public String deleteRoom(@PathVariable("roomUUID") String roomUUID){
        return roomService.deleteRoom(roomUUID);
    }

}
