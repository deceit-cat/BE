package com.capstone.backend.domain.user.service;

import com.capstone.backend.domain.user.entity.Child;
import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.repository.ChildRepository;
import com.capstone.backend.domain.user.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChildService {
    private final TeacherRepository teacherRepository;
    private final ChildRepository childRepository;

    public ChildService(TeacherRepository teacherRepository, ChildRepository childRepository) {
        this.teacherRepository = teacherRepository;
        this.childRepository = childRepository;
    }

    public void mapTeacherToChild(Teacher teacher, Parent parent) {
        List<Child> children = childRepository.findByParentUserId(parent.getUser().getId());

        for (Child child : children) {
            List<Teacher> childTeachers = teacherRepository.findByTeacherSchoolAndTeacherClassAndUser_Name(
                    child.getChildSchool(),
                    child.getChildClass(),
                    child.getTeacherName());
            for (Teacher childTeacher : childTeachers) {
                if (childTeacher.getUser().getId().equals(teacher.getUser().getId())) {
                    child.setTeacherUserId(teacher.getUser().getId());
                    childRepository.save(child);
                    break;
                }
            }
        }
    }
}