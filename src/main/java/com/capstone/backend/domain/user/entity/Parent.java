package com.capstone.backend.domain.user.entity;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "PARENTS")
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    private int childNum;

    @OneToMany(mappedBy = "parent")
    private List<Child> children = new ArrayList<>(); // 자녀

    public Parent(User user, int childNum) {
        this.user = user;
        this.user.setRole(Role.PARENT);
        this.childNum = childNum;
    }

    public void addChild(Child child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        child.setParentUserId(this.getUser().getId());
    }
}
