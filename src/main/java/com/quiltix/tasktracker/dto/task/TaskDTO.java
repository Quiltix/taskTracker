

package com.quiltix.tasktracker.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quiltix.tasktracker.model.Category;
import com.quiltix.tasktracker.model.StatusEnum;
import com.quiltix.tasktracker.model.Task;


import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class TaskDTO implements Serializable {


    public TaskDTO(Task task) {
        status = task.getStatus();
        important = task.getImportant();
        categoryId = task.getCategory() != null ? task.getCategory() : null;
        startTime = task.getStartTime();
        timeToComplete = task.getTimeToComplete();
        description = task.getDescription();
        title = task.getTitle();
        id = task.getId();
    }

    private Long id;

    @Size(min = 3, message = "Title must be at least 3 characters long")
    private String title;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime timeToComplete;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime startTime;

    private Category categoryId;

    private boolean important;

    private StatusEnum status;




}
