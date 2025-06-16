SK PJT2 Re:Use 채팅 서비스
실시간 중고거래 플랫폼을 위한 WebSocket 기반 채팅 시스템

📋 프로젝트 개요
Re:Use 중고거래 플랫폼의 실시간 채팅 서비스입니다. WebSocket과 STOMP 프로토콜을 활용하여 구매자와 판매자 간 원활한 소통을 지원하며, Kafka를 통한 확장 가능한 아키텍처를 구현했습니다.
🛠️ 기술 스택
백엔드

Java 17 - 주요 개발 언어
Spring Boot 3.4.2 - 애플리케이션 프레임워크
Spring WebSocket & STOMP - 실시간 양방향 통신
Spring Security - 보안 설정
Spring Data JPA - 데이터 액세스

데이터베이스

MySQL - 채팅 기록 및 방 정보 저장
Redis - 실시간 참여자 세션 관리

메시징 시스템

Apache Kafka - 메시지 큐 및 서버 간 데이터 통합
Zookeeper - Kafka 클러스터 관리

인프라 & 배포

Docker & Docker Compose - 컨테이너화 환경
GitHub Actions - CI/CD 파이프라인
AWS EC2 - 서버 배포

기타

Gradle - 빌드 도구
Lombok - 코드 간소화
JavaMail - 이메일 문의 기능

🏗️ 시스템 아키텍처
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Chat Server   │    │   Message Queue │
│   (WebSocket)   │◄──►│   (Spring Boot) │◄──►│     (Kafka)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │     MySQL       │    │     Redis       │
                       │  (Chat History) │    │ (Session Store) │
                       └─────────────────┘    └─────────────────┘
📁 프로젝트 구조
src/main/java/com/example/sk_pjt2_back_chat/
├── config/
│   ├── SecurityConfig.java         # Spring Security 설정
│   └── WebSocketConfig.java        # WebSocket & STOMP 설정
├── controller/
│   ├── ChatController.java         # 채팅 메시지 처리
│   ├── RoomController.java         # 채팅방 관리
│   └── InquiryController.java      # 문의 기능
├── dto/
│   ├── ChatDto.java               # 채팅 메시지 DTO
│   ├── RoomDto.java               # 채팅방 정보 DTO
│   └── RoomUserDto.java           # 방 참여자 DTO
├── entity/
│   ├── Chat.java                  # 채팅 메시지 엔티티
│   ├── Room.java                  # 채팅방 엔티티
│   └── RoomUser.java              # 방 참여자 엔티티
├── service/
│   ├── ChatService.java           # 채팅 비즈니스 로직
│   ├── RoomService.java           # 채팅방 관리 로직
│   └── InquiryService.java        # 문의 처리 로직
├── repository/
│   ├── ChatRepository.java        # 채팅 데이터 접근
│   ├── RoomRepository.java        # 채팅방 데이터 접근
│   └── RoomUserRepository.java    # 참여자 데이터 접근
└── interceptor/
    └── StompInterceptor.java      # STOMP 연결 인터셉터
🚀 주요 기능
실시간 채팅

WebSocket + STOMP 프로토콜 기반 양방향 통신
실시간 메시지 전송 및 수신
접속자 상태 관리

채팅방 관리

1:1 채팅방 자동 생성
상품별 채팅방 구분
채팅 히스토리 저장 및 조회

확장 가능한 아키�ecture

Kafka를 통한 멀티 서버 메시지 동기화
Redis 기반 세션 관리
마이크로서비스 호환 설계

보안 & 인증

Spring Security 통합
JWT 기반 사용자 인증 준비
CORS 설정으로 안전한 통신

📚 API 문서
채팅 관련
http# 채팅방 메시지 조회
GET /chat/room/{roomUUID}

# 웹소켓 연결
WebSocket: /ws-stomp
채팅방 관리
http# 채팅방 생성
POST /room/create
Headers: X-Auth-User: {userId}
Params: user={targetUser}&name={productName}

# 사용자 채팅방 목록 조회
GET /room/list
Headers: X-Auth-User: {userId}

# 채팅방 입장
GET /room/enter/{roomUUID}
Headers: X-Auth-User: {userId}

# 채팅방 삭제
DELETE /room/delete/{roomUUID}
WebSocket 메시지
javascript// 구독 (메시지 수신)
stompClient.subscribe('/sub/chat/room/{roomUUID}', callback);

// 발행 (메시지 전송)
stompClient.send('/pub/room/{roomUUID}/message', {}, JSON.stringify({
    roomUUID: 'room-id',
    sender: 'user-id',
    content: '메시지 내용'
}));
🐳 Docker 환경 설정
필수 서비스
yaml# docker-compose.yml에 포함된 서비스들
services:
  - zookeeper    # Kafka 클러스터 관리
  - kafka        # 메시지 큐
  - mongodb      # NoSQL (예비용)
실행 명령어
bash# Docker 환경 시작
docker-compose up -d

# 애플리케이션 빌드 및 실행
./gradlew clean build -x test
java -jar build/libs/sk_pjt2_back_chat-0.0.1-SNAPSHOT.jar
⚙️ 환경 설정
application.yml 예시
yamlspring:
  datasource:
    url: jdbc:mysql://localhost:3306/sk_pjt2_chat
    username: your_username
    password: your_password
  
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: team5
  
  redis:
    host: localhost
    port: 6379
  
  mail:
    host: smtp.gmail.com
    username: your_email@gmail.com
    password: your_app_password
환경 변수
GitHub Actions 배포를 위한 필수 시크릿:

EC2_HOST - EC2 서버 주소
EC2_USER - EC2 사용자명
EC2_KEY - EC2 SSH 키
APPLICATION_YML - 애플리케이션 설정 파일

🔧 개발 & 배포
로컬 개발
bash# 1. 저장소 클론
git clone <repository-url>
cd sk_pjt2_back_chat

# 2. Docker 서비스 시작
docker-compose up -d

# 3. 애플리케이션 실행
./gradlew bootRun
프로덕션 배포

main 브랜치 push 시 GitHub Actions가 자동으로 EC2에 배포
포트 8070에서 서비스 실행
무중단 배포 지원

🔄 채팅 플로우

채팅방 생성: 구매자가 판매자에게 채팅 요청
WebSocket 연결: 클라이언트가 /ws-stomp 엔드포인트로 연결
방 구독: /sub/chat/room/{roomUUID} 토픽 구독
메시지 전송: /pub/room/{roomUUID}/message로 메시지 발행
Kafka 처리: 메시지가 Kafka를 통해 모든 연결된 서버로 전파
실시간 수신: 구독중인 모든 클라이언트가 메시지 수신
이력 저장: MySQL에 채팅 이력 영구 저장

📝 TODO

 JWT 인증 시스템 완전 통합
 MongoDB 채팅 저장소 마이그레이션
 파일 첨부 기능 추가
 읽음 표시 기능
 채팅방 알림 설정
 메시지 검색 기능

👥 기여자

김민석 - 주요 개발자

📄 라이선스
This project is private and confidential.

SK PJT2 Re:Use Team | 실시간 중고거래의 새로운 경험
