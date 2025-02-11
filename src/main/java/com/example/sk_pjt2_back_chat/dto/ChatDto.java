package com.example.sk_pjt2_back_chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 채팅 모델
 * 방 번호(이름), 보낸이, 내용
 * MongoDB를 사용해 저장할 예정(NoSQL, JSON형식?)
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto implements Serializable {
    private Long roomId;
    private String sender;
    private String content;
    private Long timestamp;
}
