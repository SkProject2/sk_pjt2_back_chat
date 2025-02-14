package com.example.sk_pjt2_back_chat.entity;

import com.example.sk_pjt2_back_chat.dto.RoomDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomUUID;

    @Builder
    public Room(Long id, String roomUUID) {
        this.id = id;
        this.roomUUID = roomUUID;
    }

    public RoomDto toDto(){
        return RoomDto.builder()
                .id(id)
                .roomUUID(roomUUID)
                .build();
    }

}
