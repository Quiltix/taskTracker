package com.quiltix.tasktracker.service;

import com.quiltix.tasktracker.model.StatusEnum;
import com.quiltix.tasktracker.model.Task;
import com.quiltix.tasktracker.model.TaskRepository;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskExpirationService {

    private final TaskRepository taskRepository;
    private final CacheManager cacheManager; // внедряем CacheManager для управления кэшами

    public TaskExpirationService(TaskRepository taskRepository, CacheManager cacheManager) {
        this.taskRepository = taskRepository;
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedDelay = 60000)
    public void updateExpiredTasks(){
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = taskRepository.findByStatusAndTimeToCompleteBefore(StatusEnum.CREATED, now);
        tasks.forEach(task -> {
            task.setStatus(StatusEnum.EXPIRED);
            taskRepository.save(task);
        });


        if (cacheManager.getCache("allTasks") != null) {
            cacheManager.getCache("allTasks").clear();
        }
        if (cacheManager.getCache("tasksByCategory") != null) {
            cacheManager.getCache("tasksByCategory").clear();
        }
        if (cacheManager.getCache("tasksByStatus") != null) {
            cacheManager.getCache("tasksByStatus").clear();
        }
        if (cacheManager.getCache("importantTasks") != null) {
            cacheManager.getCache("importantTasks").clear();
        }
    }
}
