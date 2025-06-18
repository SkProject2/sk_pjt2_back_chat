package com.example.sk_pjt2_back_chat.controller;

import com.example.sk_pjt2_back_chat.dto.ChatDto;
import com.example.sk_pjt2_back_chat.service.ChatService;
import com.example.sk_pjt2_back_chat.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserSessionService userSessionService;

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

    @GetMapping("/debug/sessions")
    public Map<String, Object> getAllSessions() {
        Map<String, Object> sessionInfo = new HashMap<>();

        try {
            // 현재 서버에 접속한 사용자들
            Set<String> currentServerUsers = userSessionService.getUsersOnCurrentServer();
            sessionInfo.put("currentServerUsers", currentServerUsers);

            // 서버별 사용자 현황
            Map<String, Set<String>> serverUsers = new HashMap<>();
            // 모든 서버 조회 (간단히 1, 2만 체크)
            for (String serverId : Arrays.asList("chat-server-1", "chat-server-2")) {
                Set<String> users = userSessionService.getUsersOnServer(serverId);
                if (users != null && !users.isEmpty()) {
                    serverUsers.put(serverId, users);
                }
            }
            sessionInfo.put("serverUsers", serverUsers);

            // 개별 사용자 세션 상세 정보
            Set<String> allUsers = new HashSet<>();
            serverUsers.values().forEach(allUsers::addAll);

            Map<String, UserSessionService.SessionInfo> userDetails = new HashMap<>();
            for (String userId : allUsers) {
                UserSessionService.SessionInfo info = userSessionService.getUserSessionInfo(userId);
                if (info != null) {
                    userDetails.put(userId, info);
                }
            }
            sessionInfo.put("userDetails", userDetails);

            // 서버 정보
            sessionInfo.put("currentServerId", userSessionService.getServerId());
            sessionInfo.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            sessionInfo.put("error", e.getMessage());
        }

        return sessionInfo;
    }

    // 특정 사용자 세션 정보 조회
    @GetMapping("/debug/sessions/{userId}")
    public Map<String, Object> getUserSession(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            UserSessionService.SessionInfo sessionInfo = userSessionService.getUserSessionInfo(userId);
            if (sessionInfo != null) {
                result.put("sessionInfo", sessionInfo);
                result.put("serverUrl", userSessionService.getUserServerUrl(userId));
                result.put("isOnThisServer", userSessionService.isUserOnThisServer(userId));
            } else {
                result.put("error", "User session not found");
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }

    // 서버 상태 확인
    @GetMapping("/debug/server-status")
    public Map<String, Object> getServerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("serverId", userSessionService.getServerId());
        status.put("activeUsers", userSessionService.getUsersOnCurrentServer());
        status.put("timestamp", LocalDateTime.now());
        return status;
    }

}
