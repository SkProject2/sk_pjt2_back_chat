package com.example.sk_pjt2_back_chat.repository;

import com.example.sk_pjt2_back_chat.entity.Room;
import com.example.sk_pjt2_back_chat.entity.RoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomUserRepository extends JpaRepository<RoomUser, Long> {
    Optional<RoomUser> findByRoomAndUser(Room room, String user);

    void deleteRoomsByUser(String user);

    List<RoomUser> findAllByUser(String user);
}
