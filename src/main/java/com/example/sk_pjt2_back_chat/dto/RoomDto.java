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
    private Long seller_id;
    private Long buyer_id;

    @Builder
    public RoomDto(Long id, String name, Long seller_id, Long buyer_id) {
        this.id = id;
        this.name = name;
        this.seller_id = seller_id;
        this.buyer_id = buyer_id;
    }

    public Room toEntity(){
        return Room.builder()
                .buyer_id(this.buyer_id)
                .seller_id(this.seller_id)
                .name(this.name)
                .id(this.id)
                .build();
    }
}
