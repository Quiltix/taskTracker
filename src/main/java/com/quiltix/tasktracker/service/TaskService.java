package com.quiltix.tasktracker.service;


import com.quiltix.tasktracker.DTO.Task.CreateTaskDTO;
import com.quiltix.tasktracker.DTO.Task.EditTaskDTO;
import com.quiltix.tasktracker.DTO.Task.TaskDTO;
import com.quiltix.tasktracker.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "allTasks",key = "#authentication.name")
    public List<TaskDTO> getAllTasks(Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return taskRepository.findByOwner(user).stream().map(TaskDTO::new).toList();
    }

    @CacheEvict(value = {"allTasks", "tasksByCategory"}, allEntries = true)
    public Task createTasks(Authentication authentication, CreateTaskDTO taskDTO){
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
                .status(StatusEnum.CREATED);


        if (taskDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(taskDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            taskBuilder.category(category);
        }

        Task task = taskBuilder.build();
        return taskRepository.save(task);
    }

    @CacheEvict(value = {"allTasks", "tasksByCategory"}, allEntries = true)
    public void deleteTask(Authentication authentication, Long taskId){
        String username = authentication.getName();

        Task task = taskRepository.findById(taskId).orElseThrow(()-> new EntityNotFoundException("Task not found"));

        if(!task.getOwner().getUsername().equals(username)){
            throw new AccessDeniedException("You are not authorized to delete this task");
        }

        taskRepository.delete(task);
    }

    @CacheEvict(value = {"allTasks", "tasksByCategory"}, allEntries = true)
    public Task markTaskAsComplete(Long taskId, Authentication authentication){
        String username = authentication.getName();

        Task task = taskRepository.findById(taskId).orElseThrow(()-> new EntityNotFoundException("Task not found"));

        if (!task.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to complete this task");
        }

        task.setStatus(StatusEnum.COMPLETED);

        return taskRepository.save(task);
    }

    @CacheEvict(value = {"allTasks", "tasksByCategory"}, allEntries = true)
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

    @Cacheable(value = "tasksByCategory",key = "#authentication.name + ':' + #categoryId")
    public List<TaskDTO> getTaskByCategory(Authentication authentication, Long categoryId){

        String username = authentication.getName();

        User user = userRepository.findUserByUsername(username).orElseThrow(()-> new UsernameNotFoundException("user not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        return taskRepository.findByOwnerAndCategory(user,category).stream().map(TaskDTO::new).toList();
    }

    @Cacheable(value = "tasksByStatus",key = "#authentication.name + ':' + #status")
    public List<TaskDTO> getTaskByStatus(Authentication authentication, StatusEnum status){

        String username = authentication.getName();

        User user = userRepository.findUserByUsername(username).orElseThrow(()-> new UsernameNotFoundException("user not found"));

        return taskRepository.findByOwnerAndStatus(user,status).stream().map(TaskDTO::new).toList();
    }
}
