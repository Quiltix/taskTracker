package com.quiltix.tasktracker.service;


import com.quiltix.tasktracker.DTO.Task.CreateTaskDTO;
import com.quiltix.tasktracker.DTO.Task.EditTaskDTO;
import com.quiltix.tasktracker.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
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

    private final UndertowServletWebServerFactory undertowServletWebServerFactory;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, CategoryRepository categoryRepository,
                       UndertowServletWebServerFactory undertowServletWebServerFactory) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.undertowServletWebServerFactory = undertowServletWebServerFactory;
    }

    public List<Task> getAllTasks(Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return taskRepository.findByOwner(user);
    }

    public Task createTasks(Authentication authentication, CreateTaskDTO taskDTO) throws Exception{
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

    public Task editTask(Authentication authentication, long id, EditTaskDTO editTaskDTO){
        String username = authentication.getName();

        Task task = taskRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Task not found"));

        if (!task.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to complete this task");
        }

        if (editTaskDTO.getTitle() != null) {
            task.setTitle(editTaskDTO.getTitle());
        }
        if (editTaskDTO.getDescription() != null) {
            task.setDescription(editTaskDTO.getDescription());
        }
        if (editTaskDTO.getTimeToComplete() != null) {
            task.setTimeToComplete(editTaskDTO.getTimeToComplete());
        }
        if (editTaskDTO.getCategoryId() != null && categoryRepository.existsById(editTaskDTO.getCategoryId())) {
            task.setCategory(categoryRepository.findById(editTaskDTO.getCategoryId()).orElseThrow(()-> new EntityNotFoundException("Category not found")));
        }
        if (editTaskDTO.getImportant() != null) {
            task.setImportant(editTaskDTO.getImportant());
        }

        return taskRepository.save(task);
    }
    
    public List<Task> getTaskByCategory(Authentication authentication, Long categoryId){

        String username = authentication.getName();

        User user = userRepository.findUserByUsername(username).orElseThrow(()-> new UsernameNotFoundException("user not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        return taskRepository.findByOwnerAndCategory(user,category);
    }
}
