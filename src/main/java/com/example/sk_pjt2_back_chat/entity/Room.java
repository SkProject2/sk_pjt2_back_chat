package com.example.sk_pjt2_back_chat.entity;

import com.example.sk_pjt2_back_chat.dto.RoomDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomUUID;
    private String productName;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Chat> chatList;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<RoomUser> roomUserList;

    @Builder
    public Room(Long id, String roomUUID, String productName) {
        this.id = id;
        this.roomUUID = roomUUID;
        this.productName = productName;
    }

    public RoomDto toDto(){
        return RoomDto.builder()
                .id(id)
                .roomUUID(roomUUID)
                .build();
    }

}
