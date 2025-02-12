package com.example.sk_pjt2_back_chat.entity;

import com.example.sk_pjt2_back_chat.dto.RoomDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String user;


    @Builder
    public Room(Long id, String name, String user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    public RoomDto toDto(){
        return RoomDto.builder()
                .id(id)
                .name(name)
                .user(user)
                .build();
    }
}
