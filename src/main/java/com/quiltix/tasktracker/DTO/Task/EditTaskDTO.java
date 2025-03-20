package com.quiltix.tasktracker.DTO.Task;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.quiltix.tasktracker.model.Category;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    private Long category;

    @Future(message = "Time must be in future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeToComplete;
}
