package com.capstone.backend.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeacherStatusDto {
    private boolean duty;
    private String workStart;
    private String workEnd;
    private String disturbStart;
    private String disturbEnd;
}
