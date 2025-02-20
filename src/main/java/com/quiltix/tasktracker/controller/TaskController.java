package com.quiltix.tasktracker.controller;

import com.quiltix.tasktracker.DTO.Task.CreateTaskDTO;
import com.quiltix.tasktracker.DTO.Others.MessageDTO;
import com.quiltix.tasktracker.DTO.Task.TaskDTO;
import com.quiltix.tasktracker.model.Task;
import com.quiltix.tasktracker.model.UserRepository;
import com.quiltix.tasktracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("api/task")
public class TaskController {


    private final TaskService taskService;

    private final UserRepository userRepository;

    public TaskController(TaskService taskService,
                          UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }


    @Operation(summary = "Добавление новой задачи")
    @ApiResponse(responseCode = "200", description = "Задача успешно добавлена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @PostMapping
    public ResponseEntity<?> addTask(Authentication authentication,@Valid @RequestBody CreateTaskDTO taskDTO) {
        try{
            Task task = taskService.createTaks(authentication,taskDTO);
            return ResponseEntity.ok().body(new TaskDTO(task));
        }
        catch (Exception e){
            return ResponseEntity.status(500).body(new MessageDTO(e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех задач")
    @ApiResponse(responseCode = "200", description = "Задачи получены")
    @ApiResponse(responseCode = "400", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Server error")

    @GetMapping("/all")
    public ResponseEntity<?> getAllTasksByToken(Authentication authentication) {
       try {
           List<Task> tasks= taskService.getAllTasks(authentication);
           return ResponseEntity.ok().body(tasks);
       } catch (Exception ex){
           return ResponseEntity.status(500).body(new MessageDTO(ex.getMessage()));
       }
    }

    @Operation(summary = "Удаление задачи")
    @ApiResponse(responseCode = "200", description = "Задача удалена")
    @ApiResponse(responseCode = "400", description = "Нет задачи с таким id")
    @ApiResponse(responseCode = "401", description = "Нет прав на удаление")
    @ApiResponse(responseCode = "500", description = "Server error")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(Authentication authentication,@Valid @PathVariable long taskId) {
        try{
            taskService.deleteTask(authentication,taskId);
            return ResponseEntity.ok().body(new MessageDTO("Task deleted successfully"));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new MessageDTO(e.getMessage()));
        }
    }

    @Operation(summary = "Решение таски")
    @ApiResponse(responseCode = "200", description = "Задача решена")
    @ApiResponse(responseCode = "400", description = "Нет задачи с таким id")
    @ApiResponse(responseCode = "401", description = "Нет прав на изменение")
    @ApiResponse(responseCode = "500", description = "Server error")

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<?> completeTask(Authentication authentication,@Valid @PathVariable long taskId) {
        try{
            taskService.markTaskAsComplete(taskId,authentication);
            return ResponseEntity.ok().body(new MessageDTO("Task completed successfully"));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new MessageDTO(e.getMessage()));
        }
    }


}

