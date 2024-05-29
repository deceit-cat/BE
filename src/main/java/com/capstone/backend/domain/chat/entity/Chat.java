package com.capstone.backend.domain.chat.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Table(name = "CHATS")
@Data
@Builder
@Entity
public class Chat {
    public enum MessageType {
        ENTER, CHAT, LEAVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column
    private int hidden; // 0 == view | 1 == hidden

    @Column
    private String roomId;

    @Column
    private String sender;

    @Column
    private String message;

    @Column
    private String time;
}
