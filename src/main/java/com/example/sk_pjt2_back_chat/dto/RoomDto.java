package com.example.sk_pjt2_back_chat.dto;

import com.example.sk_pjt2_back_chat.entity.Chat;
import com.example.sk_pjt2_back_chat.entity.RoomUser;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RoomDto {
    private Long id;
    private String roomUUID;
    private String productName;

    @Builder
    public RoomDto(Long id, String roomUUID, String productName) {
        this.id = id;
        this.roomUUID = roomUUID;
        this.productName = productName;
    }
}
