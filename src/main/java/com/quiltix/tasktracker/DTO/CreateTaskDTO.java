package com.quiltix.tasktracker.DTO;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class CreateTaskDTO {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Important cannot be empty")
    private Boolean important;



    @NotNull(message = "TimeToComplete cannot be empty")
    @Future(message = "Время завершения должно быть в будущем")
    private LocalDateTime timeToComplete;



}
