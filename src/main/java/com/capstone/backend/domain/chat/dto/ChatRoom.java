package com.capstone.backend.domain.chat.dto;

import lombok.Data;
import java.util.HashMap;
import java.util.UUID;

@Data
public class ChatRoom {
    private String roomId;
    private String roomName;
    private long userCount;

    private HashMap<String, String> userList = new HashMap<>();

    public ChatRoom create(String roomName){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString(); // 채팅방 id는 랜덤하게 생성
        chatRoom.roomName = roomName;
        return chatRoom;
    }
}