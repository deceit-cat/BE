package com.capstone.backend.domain.user.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendDto {
    private Long teacherUserId;
    private String teacherName;
    private Long parentUserId;
    private String parentName;
    private String roomId;
}
