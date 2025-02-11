package com.example.sk_pjt2_back_chat.repository;

import com.example.sk_pjt2_back_chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableMongoRepositories
@Repository
public interface ChatRepository extends MongoRepository<Chat, String>, PagingAndSortingRepository<Chat, String> {
    List<Chat> findAllByRoomId(Long roomId);
}
