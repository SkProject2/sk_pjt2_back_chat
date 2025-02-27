package com.example.sk_pjt2_back_chat.controller;

import com.example.sk_pjt2_back_chat.dto.ChatDto;
import com.example.sk_pjt2_back_chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    @Autowired
    private ChatService chatService;

    // 특정 채팅방에 메세지 전송
    @MessageMapping("/room/{roomUUID}/message")
    public void message(@DestinationVariable String roomUUID, ChatDto chatDto) {
        System.out.println("ChatController: 메세지 전송 시작");
        try{
            chatService.sendMessageWithKafka(roomUUID, chatDto);
        }catch (Exception e){
            System.out.println("메세지 전송중 문제발생");
        }
    }

    // 특정 채팅방의 모든 채팅내역 조회
    @GetMapping("/room/{roomUUID}")
    public List<ChatDto> getRoom(@PathVariable("roomUUID") String roomUUID) {
        return chatService.getAllMessageById(roomUUID);
    }

    @GetMapping("/test")
    public String test() {
        return "test response";
    }

}
