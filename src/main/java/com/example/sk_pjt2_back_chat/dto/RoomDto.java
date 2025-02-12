package com.example.sk_pjt2_back_chat.dto;

import com.example.sk_pjt2_back_chat.entity.Room;
import lombok.Builder;
import lombok.Data;

/**
 * 채팅방 정보, MySQL로 관리할 예정
 */

@Data
public class RoomDto {
    private Long id;
    private String name;
    private String user;

    @Builder
    public RoomDto(Long id, String name, String user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    public Room toEntity(){
        return Room.builder()
                .user(this.user)
                .name(this.name)
                .id(this.id)
                .build();
    }
}
