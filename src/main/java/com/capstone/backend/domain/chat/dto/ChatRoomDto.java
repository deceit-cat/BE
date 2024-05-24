package com.capstone.backend.domain.chat.dto;

import lombok.Data;

@Data
public class ChatRoomDto {
    private String roomId;
    private Long teacherUserId;
    private Long parentUserId;
}