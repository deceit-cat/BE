package com.capstone.backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ChatDto {
    public enum MessageType {
        ENTER, CHAT, LEAVE
    }
    private MessageType type;
    private int hidden; // 0 == view | 1 == hidden
    private String roomId;
    private String sender;
    private String message;
    private String time;

    @JsonCreator
    public ChatDto(@JsonProperty("type") MessageType type,
                   @JsonProperty("hidden") int hidden,
                   @JsonProperty("roomId") String roomId,
                   @JsonProperty("sender") String sender,
                   @JsonProperty("message") String message,
                   @JsonProperty("time") String time) {
        this.type = type;
        this.hidden = hidden;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.time = time;
    }
}
