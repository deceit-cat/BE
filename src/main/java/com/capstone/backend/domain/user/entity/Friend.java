package com.capstone.backend.domain.user.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "FRIENDS")
@Getter
@Setter
public class Friend {

    public Friend() {}

    public Friend(Long teacherUserId, Long parentUserId, String roomId) {
        this.teacherUserId = teacherUserId;
        this.parentUserId = parentUserId;
        this.roomId = null;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_user_id")
    private Long teacherUserId;

    @Column(name = "parent_user_id")
    private Long parentUserId;

    @Column(name = "room_id")
    private String roomId;

    // 생성자, 게터, 세터 등 필요한 코드를 추가할 수 있습니다.
}
