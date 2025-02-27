package com.quiltix.tasktracker.service;


import com.quiltix.tasktracker.model.StatusEnum;
import com.quiltix.tasktracker.model.Task;
import com.quiltix.tasktracker.model.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskExpirationService {


    private final TaskRepository taskRepository;

    public TaskExpirationService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Scheduled(fixedDelay = 60000)
    public void updateExpiredTasks(){
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = taskRepository.findByStatusAndTimeToCompleteBefore(StatusEnum.CREATED, now);
        tasks.forEach(task->{
            task.setStatus(StatusEnum.EXPIRED);
            taskRepository.save(task);
        });

    }
}
