package com.capstone.backend.domain.user.entity;

import com.capstone.backend.domain.user.dto.ChildDto;
import lombok.*;

import javax.persistence.*;

@Table(name = "CHILDS")
@Entity
@Getter
@Setter
public class Child {
    public Child() { }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Parent parent;

    @Column(name = "parent_user_id")
    private Long parentUserId;

    @Column(name = "teacher_user_id")
    private Long teacherUserId; // 선생님의 User Id

    @Column
    private String childName; // 자녀 이름

    @Column
    private String childSchool; // 자녀 학교

    @Column
    private String childClass; // 자녀 반

    @Column
    private String teacherName; // 선생님 이름

    public Child(Parent parent, ChildDto dto) {
        this.childName = dto.getChildName();
        this.childSchool = dto.getChildSchool();
        this.childClass = dto.getChildClass();
        this.teacherName = dto.getTeacherName();
        this.parent = parent;
        this.parentUserId = parent.getUser().getId();
    }

    public Long getTeacherUserId() {
        return this.teacherUserId;
    }

    public void setTeacherUserId(Long teacherUserId) {
        this.teacherUserId = teacherUserId;
    }

    public String getTeacherName() {
        return teacherName;
    }
}
