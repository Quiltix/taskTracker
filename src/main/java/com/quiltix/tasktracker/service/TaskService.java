package com.quiltix.tasktracker.service;


import com.quiltix.tasktracker.DTO.Task.CreateTaskDTO;
import com.quiltix.tasktracker.DTO.Task.EditTaskDTO;
import com.quiltix.tasktracker.DTO.Task.TaskDTO;
import com.quiltix.tasktracker.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final CacheManager cacheManager;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, CategoryRepository categoryRepository,
                       CacheManager cacheManager) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cacheManager = cacheManager;
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

        boolean isImportant = Boolean.TRUE.equals(task.getImportant());

        Task updatedTask = taskRepository.save(task);

        if (isImportant){
            if(cacheManager.getCache("importantTasks")!= null){
                Objects.requireNonNull(cacheManager.getCache("importantTasks")).evict(username);
            }
        }
        if (cacheManager.getCache("tasksByStatus") != null){
            Objects.requireNonNull(cacheManager.getCache("tasksByStatus")).evict(username + ":" + StatusEnum.CREATED);
        }
        return updatedTask;
    }

    @CacheEvict(value = {"allTasks", "tasksByCategory"}, allEntries = true)
    public void deleteTask(Authentication authentication, Long taskId){
        String username = authentication.getName();

        Task task = taskRepository.findById(taskId).orElseThrow(()-> new EntityNotFoundException("Task not found"));

        if(!task.getOwner().getUsername().equals(username)){
            throw new AccessDeniedException("You are not authorized to delete this task");
        }

        boolean isImportant = Boolean.TRUE.equals(task.getImportant());
        StatusEnum status = task.getStatus();

        taskRepository.delete(task);

        if (isImportant){
            if(cacheManager.getCache("importantTasks")!= null){
                Objects.requireNonNull(cacheManager.getCache("importantTasks")).evict(username);
            }
        }
        if (cacheManager.getCache("tasksByStatus") != null){
            Objects.requireNonNull(cacheManager.getCache("tasksByStatus")).evict(username + ":" + status);
        }

    }

    @CacheEvict(value = {"allTasks", "tasksByCategory"}, allEntries = true)
    public Task markTaskAsComplete(Long taskId, Authentication authentication){
        String username = authentication.getName();

        Task task = taskRepository.findById(taskId).orElseThrow(()-> new EntityNotFoundException("Task not found"));

        if (!task.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to complete this task");
        }

        task.setStatus(StatusEnum.COMPLETED);

        Task updatedTask = taskRepository.save(task);

        StatusEnum oldStatus = task.getStatus();
        boolean isImportant = Boolean.TRUE.equals(task.getImportant());
        if (cacheManager.getCache("tasksByStatus") != null) {
            if (!oldStatus.equals(StatusEnum.COMPLETED)) {
                Objects.requireNonNull(cacheManager.getCache("tasksByStatus")).evict(username + ":" + oldStatus);
            }
            Objects.requireNonNull(cacheManager.getCache("tasksByStatus")).evict(username + ":" + StatusEnum.COMPLETED);
        }
        if (isImportant){
            if(cacheManager.getCache("importantTasks")!= null){
                Objects.requireNonNull(cacheManager.getCache("importantTasks")).evict(username);
            }
        }
        return updatedTask;
    }

    @CacheEvict(value = {"allTasks", "tasksByCategory", "tasksByStatus"}, allEntries = true)
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
        if (editTaskDTO.getCategoryId() != null ) {
            task.setCategory(categoryRepository.findById(editTaskDTO.getCategoryId()).orElseThrow(()-> new EntityNotFoundException("Category not found")));
        }
        if (editTaskDTO.getImportant() != null) {
            task.setImportant(editTaskDTO.getImportant());
        }
        Task updatedTask = taskRepository.save(task);

        if (Boolean.TRUE.equals(updatedTask.getImportant())) {
            if (cacheManager.getCache("importantTasks") != null) {
                Objects.requireNonNull(cacheManager.getCache("importantTasks")).evict(username);
            }
        }
        return updatedTask;
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

    @Cacheable(value = "importantTasks", key = "#authentication.name")
    public List<TaskDTO> getImportantTasks(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return taskRepository.findByOwnerAndImportant(user, true)
                .stream()
                .map(TaskDTO::new)
                .toList();
    }

    //

}
