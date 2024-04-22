package com.capstone.backend.domain.chat.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Entity
public class ChatRoom {
    @Id
    private Long id;

    private String roomId;

    public void create() {
        this.roomId = UUID.randomUUID().toString();
    }

    // 채팅방 유저 카운트 부분.. 필요한가?
    private int userCount;
    public int getUserCount() {
        return userCount;
    }
}
