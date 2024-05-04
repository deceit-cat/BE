package com.capstone.backend.domain.user.repository;

import com.capstone.backend.domain.user.entity.Child;
import com.capstone.backend.domain.user.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByTeacherSchoolAndTeacherClassAndUser_Name(String teacherSchool, String teacherClass, String teacherName);
    Optional<Teacher> findByUserId(Long teacherUserId);
}
