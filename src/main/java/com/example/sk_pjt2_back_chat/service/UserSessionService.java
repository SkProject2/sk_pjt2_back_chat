package com.example.sk_pjt2_back_chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class UserSessionService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${server.id:default-server}")
    private String serverId;

    @Value("${server.port:8070}")
    private String serverPort;

    @Value("${server.host:localhost}")
    private String serverHost;

    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);

    // 세션 정보 DTO
    public static class SessionInfo {
        private String userId;
        private String sessionId;
        private String serverId;
        private String serverHost;
        private String serverPort;
        private String serverUrl;
        private LocalDateTime connectTime;
        private LocalDateTime lastActiveTime;

        // 생성자, getter, setter
        public SessionInfo() {}

        public SessionInfo(String userId, String sessionId, String serverId,
                           String serverHost, String serverPort) {
            this.userId = userId;
            this.sessionId = sessionId;
            this.serverId = serverId;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
            this.serverUrl = "ws://" + serverHost + ":" + serverPort + "/ws-stomp";
            this.connectTime = LocalDateTime.now();
            this.lastActiveTime = LocalDateTime.now();
        }

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }

        public String getServerId() { return serverId; }
        public void setServerId(String serverId) { this.serverId = serverId; }

        public String getServerHost() { return serverHost; }
        public void setServerHost(String serverHost) { this.serverHost = serverHost; }

        public String getServerPort() { return serverPort; }
        public void setServerPort(String serverPort) { this.serverPort = serverPort; }

        public String getServerUrl() { return serverUrl; }
        public void setServerUrl(String serverUrl) { this.serverUrl = serverUrl; }

        public LocalDateTime getConnectTime() { return connectTime; }
        public void setConnectTime(LocalDateTime connectTime) { this.connectTime = connectTime; }

        public LocalDateTime getLastActiveTime() { return lastActiveTime; }
        public void setLastActiveTime(LocalDateTime lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    }

    // 사용자 접속 시 - 상세 정보와 함께 저장
    public void userConnect(String userId, String sessionId) {
        try {
            SessionInfo sessionInfo = new SessionInfo(userId, sessionId, serverId, serverHost, serverPort);
            String sessionJson = objectMapper.writeValueAsString(sessionInfo);

            // 사용자 -> 세션 정보 매핑
            stringRedisTemplate.opsForValue().set("user:session:" + userId, sessionJson, SESSION_TIMEOUT);

            // 세션 -> 사용자 ID 역방향 매핑 (간단하게)
            stringRedisTemplate.opsForValue().set("session:user:" + sessionId, userId, SESSION_TIMEOUT);

            // 서버별 사용자 목록 (Set 사용)
            stringRedisTemplate.opsForSet().add("server:users:" + serverId, userId);

            System.out.println("User connected: " + userId + " to server: " + serverId +
                    " (host: " + serverHost + ":" + serverPort + ")");

        } catch (JsonProcessingException e) {
            System.err.println("세션 정보 JSON 변환 오류: " + e.getMessage());
        }
    }

    // 기존 호환성 유지
    public void userConnect(String userId) {
        userConnect(userId, "unknown-session");
    }

    // 사용자 접속 해제
    public void userDisconnect(String userId) {
        // 세션 정보 삭제
        stringRedisTemplate.delete("user:session:" + userId);

        // 서버별 사용자 목록에서 제거
        stringRedisTemplate.opsForSet().remove("server:users:" + serverId, userId);

        // 역방향 매핑 찾아서 삭제
        Set<String> sessionKeys = stringRedisTemplate.keys("session:user:*");
        if (sessionKeys != null) {
            for (String sessionKey : sessionKeys) {
                String mappedUserId = stringRedisTemplate.opsForValue().get(sessionKey);
                if (userId.equals(mappedUserId)) {
                    stringRedisTemplate.delete(sessionKey);
                }
            }
        }

        System.out.println("User disconnected: " + userId);
    }

    // 세션 ID로 접속 해제
    public void disconnectBySessionId(String sessionId) {
        String userId = stringRedisTemplate.opsForValue().get("session:user:" + sessionId);

        if (userId != null) {
            userDisconnect(userId);
            System.out.println("Session cleanup: " + sessionId + " for user: " + userId);
        } else {
            System.out.println("No user found for sessionId: " + sessionId);
        }
    }

    // 특정 사용자의 세션 정보 조회
    public SessionInfo getUserSessionInfo(String userId) {
        try {
            String sessionJson = stringRedisTemplate.opsForValue().get("user:session:" + userId);
            if (sessionJson != null) {
                return objectMapper.readValue(sessionJson, SessionInfo.class);
            }
        } catch (JsonProcessingException e) {
            System.err.println("세션 정보 JSON 파싱 오류: " + e.getMessage());
        }
        return null;
    }

    // 특정 사용자가 어느 서버에 접속했는지 확인
    public String getUserServer(String userId) {
        SessionInfo sessionInfo = getUserSessionInfo(userId);
        return sessionInfo != null ? sessionInfo.getServerId() : null;
    }

    // 특정 사용자의 서버 URL 조회
    public String getUserServerUrl(String userId) {
        SessionInfo sessionInfo = getUserSessionInfo(userId);
        return sessionInfo != null ? sessionInfo.getServerUrl() : null;
    }

    // 특정 서버에 접속한 모든 사용자 조회
    public Set<String> getUsersOnServer(String serverId) {
        return stringRedisTemplate.opsForSet().members("server:users:" + serverId);
    }

    // 현재 서버에 접속한 사용자 조회
    public Set<String> getUsersOnCurrentServer() {
        return getUsersOnServer(serverId);
    }

    // 사용자가 이 서버에 접속중인지 확인
    public boolean isUserOnThisServer(String userId) {
        String userServer = getUserServer(userId);
        return serverId.equals(userServer);
    }

    // 채팅방에 이 서버에 접속한 사용자가 있는지 확인
    public boolean hasUsersInRoom(String roomUUID) {
        // 현재 서버에 접속한 모든 사용자 조회
        Set<String> currentServerUsers = getUsersOnCurrentServer();

        if (currentServerUsers == null || currentServerUsers.isEmpty()) {
            return false;
        }

        // TODO: 실제로는 roomUUID에 참여한 사용자들과 교집합을 확인해야 함
        // 지금은 간단히 현재 서버에 접속한 사용자가 있으면 true 반환
        System.out.println("서버 " + serverId + "에 접속한 사용자: " + currentServerUsers);
        return !currentServerUsers.isEmpty();
    }

    // 세션 활성화 시간 갱신
    public void refreshSession(String userId) {
        SessionInfo sessionInfo = getUserSessionInfo(userId);
        if (sessionInfo != null) {
            sessionInfo.setLastActiveTime(LocalDateTime.now());
            try {
                String sessionJson = objectMapper.writeValueAsString(sessionInfo);
                stringRedisTemplate.opsForValue().set("user:session:" + userId, sessionJson, SESSION_TIMEOUT);
            } catch (JsonProcessingException e) {
                System.err.println("세션 갱신 오류: " + e.getMessage());
            }
        }
    }

    // 서버 ID getter 추가
    public String getServerId() {
        return serverId;
    }

    // 디버깅용: 모든 세션 정보 출력
    public void printAllSessions() {
        System.out.println("=== 서버별 접속 사용자 현황 ===");

        Set<String> serverKeys = stringRedisTemplate.keys("server:users:*");
        if (serverKeys != null) {
            for (String serverKey : serverKeys) {
                String serverId = serverKey.replace("server:users:", "");
                Set<String> users = stringRedisTemplate.opsForSet().members(serverKey);
                System.out.println("서버 " + serverId + ": " + users);
            }
        }

        System.out.println("\n=== 사용자별 상세 세션 정보 ===");
        Set<String> userSessions = stringRedisTemplate.keys("user:session:*");
        if (userSessions != null) {
            for (String key : userSessions) {
                String userId = key.replace("user:session:", "");
                SessionInfo info = getUserSessionInfo(userId);
                if (info != null) {
                    System.out.println(userId + " -> 서버: " + info.getServerId() +
                            ", URL: " + info.getServerUrl() +
                            ", 접속시간: " + info.getConnectTime());
                }
            }
        }
    }
}