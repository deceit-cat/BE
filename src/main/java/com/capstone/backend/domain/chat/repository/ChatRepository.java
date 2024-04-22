package com.capstone.backend.domain.chat.repository;

import com.capstone.backend.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

//    @Query("SELECT DISTINCT c FROM ChatRoom c JOIN c.friends f WHERE f.id = :userId")
//    List<ChatRoom> findUserChatRooms(@Param("user") User user);

//    private Map<String, ChatRoom> chatRoomMap;

//    @PostConstruct
//    private void init() {
//        chatRoomMap = new LinkedHashMap<>();
//    }
//

}