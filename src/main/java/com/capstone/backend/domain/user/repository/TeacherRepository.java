package com.capstone.backend.domain.user.repository;

import com.capstone.backend.domain.user.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findByTeacherSchoolAndTeacherClass(String teacherSchool, String teacherClass);
    Optional<Teacher> findByUserId(Long teacherUserId);
}
