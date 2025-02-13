package com.example.sk_pjt2_back_chat.entity;

import com.example.sk_pjt2_back_chat.dto.ChatDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅 내용을 관리하는 Entity
 * 방정보를 기준으로 전송된 내용가 보낸이를 저장, 필요하면 보낸 시각도 저장해서 순서를 보장할 생각도 있음
 * 나중에 방이 사라지면 관련 채팅도 다 지워버리는 CASCADE도 설정할 것(지금은 테스트 해야하므로 생략)
 * 통신용 DTO를 별도 구성하여 통신된 내용은 그대로 전송하고 중간에 인터샙트하여 저장
 */

@Entity
@NoArgsConstructor
@Data
public class Chat {
    @Id
    private String id;
    private String roomUUID;
    private String sender;
    private String message;
    private Long timestamp;

    @Builder
    public Chat(String id, String roomUUID, String sender, String message, Long timestamp) {
        this.id = id;
        this.roomUUID = roomUUID;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ChatDto toDto(){
        return ChatDto.builder()
                .roomUUID(this.roomUUID)
                .sender(this.sender)
                .content(this.message)
                .timestamp(this.timestamp)
                .build();
    }
}
