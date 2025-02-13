package com.example.sk_pjt2_back_chat.repository;

import com.example.sk_pjt2_back_chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomUUIDAndUser(String roomUUID, String user);

    void deleteRoomsByUser(String user);
}
