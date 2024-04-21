package com.capstone.backend.domain.chat.dto;

import com.capstone.backend.domain.user.entity.Friend;
import com.capstone.backend.domain.user.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;
    private long userCount;

    public ChatRoom create() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        return chatRoom;
    }

//    public int getUserCount() {
//        return friends.size();
//    }
}