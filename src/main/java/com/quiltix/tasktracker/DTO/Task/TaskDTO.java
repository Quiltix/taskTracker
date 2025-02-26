

package com.quiltix.tasktracker.DTO.Task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quiltix.tasktracker.model.Category;
import com.quiltix.tasktracker.model.StatusEnum;
import com.quiltix.tasktracker.model.Task;


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
        category = task.getCategory() != null ? task.getCategory().getId() : null;;
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

    private Long category;

    private boolean important;

    private StatusEnum status;




}
