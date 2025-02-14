package com.example.sk_pjt2_back_chat.dto;

import com.example.sk_pjt2_back_chat.entity.Room;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomDto {
    private Long id;
    private String roomUUID;

    @Builder
    public RoomDto(Long id, String roomUUID) {
        this.id = id;
        this.roomUUID = roomUUID;
    }
}
