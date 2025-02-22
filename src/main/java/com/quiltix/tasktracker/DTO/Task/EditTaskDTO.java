package com.quiltix.tasktracker.DTO.Task;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.quiltix.tasktracker.model.Category;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EditTaskDTO {

    private String title;

    private String description;

    private Boolean important;

    private Long categoryId;

    @Future(message = "Time must be in future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime timeToComplete;
}
