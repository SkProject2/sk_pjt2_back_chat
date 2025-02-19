package com.example.sk_pjt2_back_chat.repository;

import com.example.sk_pjt2_back_chat.entity.Chat;
import com.example.sk_pjt2_back_chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByRoomOrderByTimestampAsc(Room room);
}
