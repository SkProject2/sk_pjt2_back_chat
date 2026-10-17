# Re:Use Chat Service

> 실시간 중고거래 플랫폼을 위한 WebSocket(STOMP) + Redis Pub/Sub 기반 분산 채팅 서비스

## 🚀 Overview

Re:Use 중고거래 플랫폼의 채팅 서비스입니다. WebSocket과 STOMP 프로토콜로 실시간 양방향 통신을 제공하며, **Redis Pub/Sub 채팅방 단위 채널**로 여러 서버 인스턴스 간 메시지를 전달합니다.

초기에는 Kafka 단일 토픽으로 모든 서버에 메시지를 fan-out하는 구조였지만, 채팅 전달에는 Kafka가 무겁다고 판단해 **Redis Pub/Sub 구조로 변경**했습니다. Kafka는 다른 서비스와의 이벤트 연동(채팅 후속 처리)에 사용합니다.

### ✨ Key Features

- **🔄 실시간 채팅**: WebSocket + STOMP 기반 메시징
- **🌐 분산 서버 지원**: Redis Pub/Sub 채팅방 채널(`chat:room:{roomUUID}`)로 서버 간 메시지 전달
- **🎯 채널 구독 최적화**: 해당 채팅방 사용자가 접속한 서버만 채널을 구독하고, 마지막 사용자가 나가면 구독 해제
- **📱 1:1 채팅방**: 구매자-판매자 간 개별 채팅방 자동 생성
- **💾 채팅 히스토리**: 모든 메시지를 MySQL에 저장해, 미접속 사용자도 재접속 시 이전 대화 확인
- **⚡ 세션 관리**: Redis를 활용한 실시간 사용자 세션 추적 (JSON 기반)
- **🔗 서비스 연동**: Kafka로 다른 서비스와 이벤트 연동 (채팅 후속 처리)
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
- **Redis** - Pub/Sub Message Broker & Session Store (JSON Format)
- **Apache Kafka** - Inter-service Event Communication
- **Docker Compose** - Development Environment

### DevOps
- **GitHub Actions** - CI/CD Pipeline
- **AWS EC2** - Production Deployment
- **Gradle** - Build & Dependency Management

---

## 🏗️ Architecture

### 분산 채팅 아키텍처
```
[클라이언트A] ──STOMP──→ [서버1] ──Publish──→ [Redis Channel] ──Subscribe──→ [서버2] ──STOMP──→ [클라이언트B]
     ↑                     ↓                chat:room:{roomUUID}                          ↑
  WebSocket            MySQL 저장                                                      WebSocket
     ↑                 (모든 메시지)                                                       ↑
localhost:8070                                                                     localhost:8071

[다른 서비스] ──Kafka 이벤트──→ [채팅 서버]  (채팅 후속 처리)
```

### 핵심 구성요소

#### 1. **STOMP (클라이언트 ↔ 서버)**
- WebSocket 기반 실시간 양방향 통신
- 클라이언트와 각 서버 간 연결 유지
- 구독·해제 이벤트를 `StompInterceptor`에서 처리

#### 2. **Redis Pub/Sub (서버 ↔ 서버)**
- 채팅방마다 별도 채널(`chat:room:{roomUUID}`)로 메시지 발행
- `RoomSubscriptionManager`가 서버별 채팅방 구독자 수를 관리
- 서버에 해당 채팅방의 첫 구독자가 생기면 채널 구독, 마지막 구독자가 나가면 구독 해제

#### 3. **Kafka (서비스 ↔ 서비스)**
- 다른 서비스와의 이벤트 연동
- 채팅 후속 처리에 사용

#### 4. **Redis (세션 관리)**
- JSON 형태의 사용자 세션 정보 저장
- 서버별 접속 사용자 추적

---

## 🗂️ Project Structure

```
src/main/java/com/example/sk_pjt2_back_chat/
├── 📁 config/           # 설정 파일들
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java      # STOMP 설정
│   └── RedisConfig.java          # Redis 설정 (Pub/Sub, JSON 직렬화)
├── 📁 controller/       # REST & WebSocket 컨트롤러
│   ├── ChatController.java       # 채팅 + 디버그 API
│   ├── RoomController.java       # 채팅방 관리
│   └── InquiryController.java    # 문의 처리
├── 📁 service/          # 비즈니스 로직
│   ├── ChatService.java              # 메시지 저장 + Redis Publish
│   ├── RoomSubscriptionManager.java  # 서버별 채팅방 채널 구독 관리
│   ├── RoomService.java              # 채팅방 서비스
│   ├── RoomUserService.java          # 채팅방-사용자 관리
│   ├── InquiryService.java           # 문의 메일 전송
│   └── UserSessionService.java       # Redis 세션 관리 (JSON)
├── 📁 subscriber/       # Redis 메시지 수신
│   └── RedisChatSubscriber.java      # 채널 메시지 → STOMP 전달
├── 📁 entity/           # JPA 엔티티
│   ├── Chat.java                 # 채팅 메시지
│   ├── Room.java                 # 채팅방
│   └── RoomUser.java             # 채팅방-사용자 매핑
├── 📁 dto/              # 데이터 전송 객체
├── 📁 repository/       # 데이터 액세스 계층
└── 📁 interceptor/      # STOMP 인터셉터
    └── StompInterceptor.java     # 세션 ID 기반 연결/구독/해제 처리
```

---

## 🔄 Message Flow

### 멀티서버 채팅 메시지 흐름

1. **사용자 A (서버1)** → **메시지 전송** → **STOMP 수신**
2. **서버1** → **MySQL 저장** (모든 메시지)
3. **서버1** → **Redis 채널** `chat:room:{roomUUID}`에 발행
4. **해당 채널을 구독 중인 서버**만 메시지 수신
5. **서버2** → **STOMP로 실시간 전송**
6. **사용자 B (서버2)** → **즉시 메시지 수신**
7. 사용자 B가 미접속이면 → 재접속 시 **MySQL에서 이전 대화 조회**

### 채널 구독 흐름

1. **STOMP SUBSCRIBE** → `StompInterceptor`가 감지 → `RoomSubscriptionManager`에 구독 등록
2. **서버의 첫 구독자** → Redis 채널 구독 시작
3. **UNSUBSCRIBE / DISCONNECT** → 구독자 수 감소
4. **서버의 마지막 구독자 이탈** → Redis 채널 구독 해제

### 세션 관리 흐름

1. **WebSocket 연결** → `StompInterceptor`가 감지
2. **Redis 저장** → JSON 형태의 세션 정보 (서버ID, URL, 접속시간 등)
3. **연결 해제** → 세션 ID 기반으로 정리

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

### Redis 채널 구조
```
chat:room:{roomUUID}
```
- 채팅방마다 독립된 채널
- 해당 채팅방 사용자가 접속한 서버만 구독
- 관계없는 서버로 메시지가 전파되지 않음

### 설계 변경: Kafka fan-out → Redis Pub/Sub
| | 기존 (Kafka) | 변경 (Redis Pub/Sub) |
| --- | --- | --- |
| 전달 방식 | 단일 토픽 → 모든 서버에 전파 | 채팅방 채널 → 구독 중인 서버만 수신 |
| 서버 구분 | 서버별 Consumer Group | 서버별 채널 구독 관리 |
| 변경 이유 | - | 실시간 채팅 전달에 Kafka는 무겁고, 저지연 전달에 Redis가 적합 |

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
