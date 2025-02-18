package com.example.sk_pjt2_back_chat.dto;

import com.example.sk_pjt2_back_chat.entity.Room;
import com.example.sk_pjt2_back_chat.entity.RoomUser;
import lombok.Builder;
import lombok.Data;

/**
 * 채팅방 정보, MySQL로 관리할 예정
 */

@Data
public class RoomUserDto {
    private Long id;
    private Room room;
    private String user;
    private String other;

    @Builder
    public RoomUserDto(Long id, Room room, String user, String other) {
        this.id = id;
        this.room = room;
        this.user = user;
        this.other = other;
    }

    public RoomUser toEntity(){
        return RoomUser.builder()
                .user(this.user)
                .room(this.room)
                .id(this.id)
                .other(this.other)
                .build();
    }
}
