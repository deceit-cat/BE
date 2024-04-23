package com.capstone.backend.domain.chat.entity;

import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Teacher;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Table(name = "CHATROOMS")
@Entity
@Getter
@Setter
public class ChatRoom {
    @Id
    @Column(name = "room_id")
    private String roomId;

    @OneToOne
    private Teacher teacher;

    @OneToOne
    private Parent parent;

    private int userCount;

    public ChatRoom() {
        this.roomId = UUID.randomUUID().toString();
    }

    public int getUserCount() {
        if (teacher != null && parent != null) {
            return 2;
        } else if (teacher != null || parent != null) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
