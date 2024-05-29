package com.capstone.backend.domain.chat.dto;

import com.capstone.backend.domain.chat.entity.Chat;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatDto {
    private Chat.MessageType type;
    private int hidden; // 0 == view | 1 == hidden
    private String roomId;
    private String sender;
    private String message;
    private String time;

    public static ChatDto from(Chat chat) {
        return ChatDto.builder()
                .type(chat.getType())
                .hidden(chat.getHidden())
                .roomId(chat.getRoomId())
                .sender(chat.getSender())
                .message(chat.getMessage())
                .time(chat.getTime())
                .build();
    }
}
