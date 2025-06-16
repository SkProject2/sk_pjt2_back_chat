package com.example.sk_pjt2_back_chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserSessionService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate; // 변경

    // 서버 ID (application.yml에서 설정)
    @Value("${server.id:default-server}")
    private String serverId;

    // 사용자 접속 시
    public void userConnect(String userId) {
        stringRedisTemplate.opsForValue().set("user:session:" + userId, serverId);
        System.out.println("User connected: " + userId + " to server: " + serverId);
    }

    // 사용자 접속 해제 시
    public void userDisconnect(String userId) {
        stringRedisTemplate.delete("user:session:" + userId);
        System.out.println("User disconnected: " + userId);
    }

    // 사용자가 이 서버에 접속중인지 확인
    public boolean isUserOnThisServer(String userId) {
        String userServer = stringRedisTemplate.opsForValue().get("user:session:" + userId);
        return serverId.equals(userServer);
    }

    // 채팅방에 이 서버에 접속한 사용자가 있는지 확인
    public boolean hasUsersInRoom(String roomUUID) {
        // 일단 true로 반환 (나중에 채팅방 참여자 체크 로직 추가)
        return true;
    }
}