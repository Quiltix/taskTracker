package com.quiltix.taskTracker.DTO;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class CreateTaskDTO {

    private String title;

    private String description;

    private LocalDateTime timeToComplete;

}
