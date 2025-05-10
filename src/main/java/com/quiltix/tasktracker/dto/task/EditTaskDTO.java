package com.quiltix.tasktracker.dto.task;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EditTaskDTO {

    @Size(min = 3, message = "title must be at least 3 characters long")
    private String title;

    private String description;

    private Boolean important;

    private Long categoryId;

    @Future(message = "Time must be in future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeToComplete;
}
