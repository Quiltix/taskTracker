package com.quiltix.taskTracker.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.quiltix.taskTracker.model.Category;
import com.quiltix.taskTracker.model.Task;
import com.quiltix.taskTracker.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskDTO {


    public TaskDTO(Task task) {
        complete = task.getComplete();
        important = task.getImportant();
        category = task.getCategory();
        startTime = task.getStartTime();
        timeToComplete = task.getTimeToComplete();
        description = task.getDescription();
        title = task.getTitle();
        id = task.getId();
    }

    private Long id;
    private String title;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime timeToComplete;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime startTime;
    private Category category;
    private boolean important;
    private boolean complete;




}
