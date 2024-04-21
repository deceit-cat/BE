package com.capstone.backend.domain.chat.repository;

import com.capstone.backend.domain.chat.dto.ChatRoom;
import com.capstone.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ChatRepository extends JpaRepository<ChatRoom, Long> {

//    @Query("SELECT DISTINCT c FROM ChatRoom c JOIN c.friends f WHERE f.id = :userId")
//    List<ChatRoom> findUserChatRooms(@Param("user") User user);

//    private Map<String, ChatRoom> chatRoomMap;

//    @PostConstruct
//    private void init() {
//        chatRoomMap = new LinkedHashMap<>();
//    }
//

}