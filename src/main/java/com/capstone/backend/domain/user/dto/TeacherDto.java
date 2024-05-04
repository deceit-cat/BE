package com.capstone.backend.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeacherDto {
    private Long teacherId;
    private String teacherName;
    private String teacherSchool;
    private String teacherClass;
    private String childName;
}
