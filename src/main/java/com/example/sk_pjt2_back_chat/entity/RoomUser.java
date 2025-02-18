package com.example.sk_pjt2_back_chat.entity;

import com.example.sk_pjt2_back_chat.dto.RoomUserDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방 관련 정보를 저장하는 Entity
 * 채팅방 자체만 관리하며, 참여한 유저는 관리 안함
 *  이 정보는 별도 테이블을 만들어서 관리할 예정(유저ID, 방ID를 복합키로 설정)
 */

@Entity
@NoArgsConstructor
@Data
public class RoomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room; // 방이름
    private String user;    // 사용자명
    private String other;


    @Builder
    public RoomUser(Long id, Room room, String user, String other) {
        this.id = id;
        this.room = room;
        this.user = user;
        this.other = other;
    }

    public RoomUserDto toDto(){
        return RoomUserDto.builder()
                .id(id)
                .room(room)
                .user(user)
                .other(other)
                .build();
    }
}
