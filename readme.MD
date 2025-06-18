# Re:Use Chat Service

> 실시간 중고거래 플랫폼을 위한 WebSocket(STOMP) + Kafka 기반 분산 채팅 마이크로서비스

## 🚀 Overview

Re:Use 중고거래 플랫폼의 핵심 채팅 서비스입니다. WebSocket과 STOMP 프로토콜을 활용한 실시간 양방향 통신을 제공하며, **Kafka를 통한 서버간 메시지 동기화**로 완전한 분산 아키텍처를 구현했습니다.

### ✨ Key Features

- **🔄 실시간 채팅**: WebSocket + STOMP 기반 즉석 메시징
- **🌐 분산 서버 지원**: Kafka를 통한 멀티서버 메시지 동기화
- **📱 1:1 채팅방**: 구매자-판매자 간 개별 채팅방 자동 생성
- **💾 채팅 히스토리**: MySQL 기반 채팅 기록 영구 저장
- **⚡ 세션 관리**: Redis를 활용한 실시간 사용자 세션 추적 (JSON 기반)
- **🔗 확장성**: 서버 개수에 상관없이 모든 사용자 간 실시간 통신
- **📧 문의 기능**: JavaMail을 통한 고객 문의 처리

---

## 🛠️ Tech Stack

### Backend
- **Java 17** - Core Language
- **Spring Boot 3.4.2** - Application Framework
- **Spring WebSocket & STOMP** - Real-time Client Communication
- **Spring Data JPA** - Data Access

### Infrastructure
- **MySQL** - Chat History & Room Management
- **Redis** - Session Store & User Tracking (JSON Format)
- **Apache Kafka** - Message Queue & Inter-service Communication
- **Docker Compose** - Development Environment

### DevOps
- **GitHub Actions** - CI/CD Pipeline
- **AWS EC2** - Production Deployment
- **Gradle** - Build & Dependency Management

---

## 🏗️ Architecture

### 분산 채팅 아키텍처
```
[클라이언트A] ──STOMP──→ [서버1] ──Kafka──→ [Kafka Broker] ──Kafka──→ [서버2] ──STOMP──→ [클라이언트B]
     ↑                     ↓                                     ↓                     ↑
  WebSocket            Producer                               Consumer              WebSocket
     ↑                     ↓                                     ↓                     ↑
localhost:8070         MySQL 저장                          MySQL 조회           localhost:8071
                       Redis 세션                          Redis 세션
```

### 핵심 구성요소

#### 1. **STOMP (클라이언트 ↔ 서버)**
- WebSocket 기반 실시간 양방향 통신
- 클라이언트와 각 서버 간 연결 유지
- 메시지 수신 및 전달

#### 2. **Kafka (서버 ↔ 서버)**
- 서버 간 메시지 동기화
- 모든 서버 인스턴스로 메시지 브로드캐스트
- 메시지 영속성 및 확장성 보장

#### 3. **Redis (세션 관리)**
- JSON 형태의 사용자 세션 정보 저장
- 서버별 접속 사용자 추적
- 실시간 세션 상태 관리

---

## 🗂️ Project Structure

```
src/main/java/com/example/sk_pjt2_back_chat/
├── 📁 config/           # 설정 파일들
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java      # STOMP 설정
│   └── RedisConfig.java          # JSON 직렬화 설정
├── 📁 controller/       # REST & WebSocket 컨트롤러
│   ├── ChatController.java       # 채팅 + 디버그 API
│   ├── RoomController.java       # 채팅방 관리
│   └── InquiryController.java    # 문의 처리
├── 📁 service/          # 비즈니스 로직
│   ├── ChatService.java          # Kafka Producer/Consumer
│   ├── RoomService.java          # 채팅방 서비스
│   └── UserSessionService.java   # Redis 세션 관리 (JSON)
├── 📁 entity/           # JPA 엔티티
│   ├── Chat.java                 # 채팅 메시지
│   ├── Room.java                 # 채팅방
│   └── RoomUser.java             # 채팅방-사용자 매핑
├── 📁 dto/              # 데이터 전송 객체
├── 📁 repository/       # 데이터 액세스 계층
└── 📁 interceptor/      # STOMP 인터셉터
    └── StompInterceptor.java     # 세션 ID 기반 연결/해제 처리
```

---

## 🔄 Message Flow

### 멀티서버 채팅 메시지 흐름

1. **사용자 A (서버1)** → **메시지 전송** → **STOMP 수신**
2. **서버1** → **MySQL 저장** + **Kafka Producer**로 발행
3. **Kafka Broker** → **모든 서버**에 메시지 전파
4. **각 서버** → **해당 서버 접속 사용자 확인** (Redis 조회)
5. **접속 사용자 있는 서버** → **STOMP로 실시간 전송**
6. **사용자 B (서버2)** → **즉시 메시지 수신**

### 세션 관리 흐름

1. **WebSocket 연결** → `StompInterceptor`가 감지
2. **Redis 저장** → JSON 형태의 세션 정보 (서버ID, URL, 접속시간 등)
3. **연결 해제** → 세션 ID 기반으로 안전한 정리

---

## 🌐 Multi-Server Support

### 서버별 설정
```yaml
# Server 1
server:
  id: chat-server-1
  port: 8070

# Server 2  
server:
  id: chat-server-2
  port: 8071
```

### Kafka Consumer Group
```java
@KafkaListener(topics = "chat", groupId = "team5-${server.id}")
```
- 각 서버별로 다른 Consumer Group
- 모든 서버가 동일한 메시지 수신
- 서버별 독립적인 메시지 처리

### Redis 세션 구조
```json
{
  "userId": "user123",
  "sessionId": "session-abc-123", 
  "serverId": "chat-server-1",
  "serverHost": "localhost",
  "serverPort": "8070",
  "serverUrl": "ws://localhost:8070/ws-stomp",
  "connectTime": "2025-01-20T10:30:00",
  "lastActiveTime": "2025-01-20T10:35:00"
}
```

---

## 🔍 Debug & Monitoring

### 실시간 세션 현황 API

- **📊 전체 세션 현황**: 서버별 접속 사용자 분포
- **👤 내 세션 정보**: 개별 사용자 세션 상세 정보
- **🔍 서버 상태 확인**: 서버 응답성 테스트
- **실시간 로그**: 타임스탬프 포함 상세 로깅

---