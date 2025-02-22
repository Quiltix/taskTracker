package com.quiltix.tasktracker.DTO.Task;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.quiltix.tasktracker.model.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EditTaskDTO {


    private String title;

    private String description;

    private boolean important;

    private Category category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime timeToComplete;
}
