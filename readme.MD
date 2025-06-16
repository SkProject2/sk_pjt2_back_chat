# Re:Use Chat Service

> 실시간 중고거래 플랫폼을 위한 WebSocket(STOMP) 기반 채팅 마이크로서비스

## 🚀 Overview

Re:Use 중고거래 플랫폼의 핵심 채팅 서비스입니다. WebSocket과 STOMP 프로토콜을 활용한 실시간 양방향 통신을 제공하며, Kafka를 통한 확장 가능한 마이크로서비스 아키텍처로 구현되었습니다.

### ✨ Key Features

- **🔄 실시간 채팅**: WebSocket + STOMP 기반 즉석 메시징
- **📱 1:1 채팅방**: 구매자-판매자 간 개별 채팅방 자동 생성
- **💾 채팅 히스토리**: MySQL 기반 채팅 기록 영구 저장
- **⚡ 세션 관리**: Redis를 활용한 실시간 사용자 세션 추적
- **🔗 확장성**: Kafka 기반 멀티 서버 메시지 동기화
- **📧 문의 기능**: JavaMail을 통한 고객 문의 처리

---

## 🛠️ Tech Stack

### Backend
- **Java 17** - Core Language
- **Spring Boot 3.4.2** - Application Framework
- **Spring WebSocket & STOMP** - Real-time Communication
- **Spring Security** - Security Layer
- **Spring Data JPA** - Data Access

### Infrastructure
- **MySQL** - Chat History & Room Management
- **Redis** - Session Store & Caching
- **Apache Kafka** - Message Queue & Inter-service Communication
- **Docker Compose** - Development Environment

### DevOps
- **GitHub Actions** - CI/CD Pipeline
- **AWS EC2** - Production Deployment
- **Gradle** - Build & Dependency Management

### Backend
- **Java 17** - Core Language
- **Spring Boot 3.4.2** - Application Framework
- **Spring WebSocket & STOMP** - Real-time Communication
- **Spring Security** - Security Layer
- **Spring Data JPA** - Data Access

### Infrastructure
- **MySQL** - Chat History & Room Management
- **Redis** - Session Store & Caching
- **Apache Kafka** - Message Queue & Inter-service Communication
- **Docker Compose** - Development Environment

### DevOps
- **GitHub Actions** - CI/CD Pipeline
- **AWS EC2** - Production Deployment
- **Gradle** - Build & Dependency Management

---

## 🗂️ Project Structure

```
src/main/java/com/example/sk_pjt2_back_chat/
├── 📁 config/           # 설정 파일들
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   └── RedisConfig.java
├── 📁 controller/       # REST & WebSocket 컨트롤러
│   ├── ChatController.java
│   ├── RoomController.java
│   └── InquiryController.java
├── 📁 service/          # 비즈니스 로직
│   ├── ChatService.java
│   ├── RoomService.java
│   └── UserSessionService.java
├── 📁 entity/           # JPA 엔티티
│   ├── Chat.java
│   ├── Room.java
│   └── RoomUser.java
├── 📁 dto/              # 데이터 전송 객체
├── 📁 repository/       # 데이터 액세스 계층
└── 📁 interceptor/      # STOMP 인터셉터
```
---
## 🔄 Message Flow

1. **채팅방 생성** → 구매자가 상품에 대해 판매자와 채팅 시작
2. **WebSocket 연결** → 클라이언트가 `/ws-stomp` 엔드포인트로 연결
3. **방 구독** → `/sub/chat/room/{roomUUID}` 토픽 구독
4. **메시지 전송** → `/pub/room/{roomUUID}/message`로 메시지 발행
5. **Kafka 브로드캐스트** → 메시지가 모든 서버 인스턴스로 전파
6. **실시간 수신** → 구독 중인 클라이언트들이 즉시 메시지 수신
7. **영구 저장** → MySQL에 채팅 히스토리 저장