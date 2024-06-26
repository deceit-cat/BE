package com.capstone.backend.domain.chat.repository;

import com.capstone.backend.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByRoomId(String roomId);
    int countUsersByRoomId(String roomId);
    void deleteByRoomId(String roomId);
}
