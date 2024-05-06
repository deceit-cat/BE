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

    public Friend(Teacher teacher, Parent parent, String roomId) {
        this.teacher = teacher;
        this.teacherUserId = teacher.getUser().getId();
        this.teacherName = teacher.getUser().getName();
        this.teacherId = teacher.getId();
        this.parent = parent;
        this.parentUserId = parent.getUser().getId();
        this.parentName = parent.getUser().getName();
        this.parentId = parent.getId();
        this.roomId = null;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    @Column(name = "teacher_user_id")
    private Long teacherUserId;

    @Column(name = "teacher_name")
    private String teacherName;

    @Column(name = "teacher_id")
    private Long teacherId;

    @ManyToOne
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Parent parent;

    @Column(name = "parent_user_id")
    private Long parentUserId;

    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "room_id")
    private String roomId;
}
