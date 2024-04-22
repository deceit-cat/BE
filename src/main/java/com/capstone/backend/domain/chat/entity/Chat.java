package com.capstone.backend.domain.chat.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Chat {
    public enum MessageType {
        ENTER, CHAT, LEAVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MessageType type;
    private int hidden; // 0 == view | 1 == hidden
    private String roomId;
    private String sender;
    private String message;
    private String time;
}
