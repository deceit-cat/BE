package com.capstone.backend.domain.chat.dto;

import com.capstone.backend.domain.user.entity.Friend;
import lombok.Data;

@Data
public class ChatRoomDto {
    private String roomId;
    private Friend teacher;
    private Friend parent;
    private int userCount;
}