package com.capstone.backend.domain.chat.entity;

import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Teacher;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;
import java.util.UUID;

@Table(name = "CHATROOMS")
@Entity
@Getter
@Setter
public class ChatRoom {
    @Id
    @Column
    private String roomId;

    @OneToOne
    private Teacher teacher;

    @Column(name = "teacher_user_id")
    private Long teacherUserId;

    @OneToOne
    private Parent parent;

    @Column(name = "parent_user_id")
    private Long parentUserId;

    public ChatRoom() {
        this.roomId = UUID.randomUUID().toString();
    }

    public ChatRoom(Teacher teacher, Parent parent) {
        this();
        this.teacher = teacher;
        this.teacherUserId = teacher.getUser().getId();
        this.parent = parent;
        this.parentUserId = parent.getUser().getId();
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
