package com.quiltix.tasktracker.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MessageDTO {
    private String Message;

    public MessageDTO(String message) {
        Message = message;
    }
}
