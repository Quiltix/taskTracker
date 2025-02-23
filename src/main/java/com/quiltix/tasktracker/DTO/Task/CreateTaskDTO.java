
package com.quiltix.tasktracker.DTO.Task;



import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class CreateTaskDTO {

    @Size(min = 3, message = "title must be at least 3 characters long")
    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Important cannot be empty")
    private Boolean important;


    private Long categoryId;

    @NotNull(message = "TimeToComplete cannot be empty")
    @Future(message = "Time must be in future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeToComplete;



}
