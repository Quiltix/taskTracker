package com.quiltix.tasktracker.service;


import com.quiltix.tasktracker.DTO.Task.CreateTaskDTO;
import com.quiltix.tasktracker.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Task> getAllTasks(Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return taskRepository.findByOwner(user);
    }

    public Task createTaks(Authentication authentication, CreateTaskDTO taskDTO) throws Exception{
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Task.TaskBuilder taskBuilder = Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .important(taskDTO.getImportant())
                .timeToComplete(taskDTO.getTimeToComplete())
                .startTime(LocalDateTime.now())
                .owner(user)
                .complete(false);


        if (taskDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(taskDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            taskBuilder.category(category);
        }

        Task task = taskBuilder.build();
        return taskRepository.save(task);
    }

    public void deleteTask(Authentication authentication, Long taskId){
        String username = authentication.getName();

        Task task = taskRepository.findById(taskId).orElseThrow(()-> new EntityNotFoundException("Task not found"));

        if(!task.getOwner().getUsername().equals(username)){
            throw new AccessDeniedException("You are not authorized to delete this task");
        }

        taskRepository.delete(task);
    }

    public Task markTaskAsComplete(Long taskId, Authentication authentication){
        String username = authentication.getName();

        Task task = taskRepository.findById(taskId).orElseThrow(()-> new EntityNotFoundException("Task not found"));

        if (!task.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to complete this task");
        }

        task.setComplete(true);

        return taskRepository.save(task);
    }


}
