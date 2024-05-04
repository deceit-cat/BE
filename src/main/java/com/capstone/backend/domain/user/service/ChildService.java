package com.capstone.backend.domain.user.service;

import com.capstone.backend.domain.user.entity.Child;
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

    public void mapTeacherToChild(Long teacherUserId, Long parentUserId) {
        // teacherID와 parentID를 사용하여 해당 child를 찾음
        List<Child> children = childRepository.findByParentUserId(parentUserId);

        for (Child child : children) {
            List<Teacher> foundTeachers = teacherRepository.findByTeacherSchoolAndTeacherClassAndUser_Name(
                    child.getChildSchool(),
                    child.getChildClass(),
                    child.getTeacherName());
            for (Teacher teacher : foundTeachers) {
                if (child.getTeacherUserId() == null && child.getTeacherName().equals(teacher.getTeacherName())) {
                    child.setTeacherUserId(teacherUserId);
                    childRepository.save(child);
                }
            }
        }
    }
}