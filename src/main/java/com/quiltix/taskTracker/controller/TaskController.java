package com.quiltix.taskTracker.controller;

import com.quiltix.taskTracker.DTO.Task.CreateTaskDTO;
import com.quiltix.taskTracker.DTO.Misc.MessageDTO;
import com.quiltix.taskTracker.DTO.Task.TaskDTO;
import com.quiltix.taskTracker.model.Task;
import com.quiltix.taskTracker.model.UserRepository;
import com.quiltix.taskTracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("api/task")
public class TaskController {


    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @Operation(summary = "Добавление новой задачи")
    @ApiResponse(responseCode = "200", description = "Задача успешно добавлена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @PostMapping("/add")
    public ResponseEntity<?> addTask(Authentication authentication,@Valid @RequestBody CreateTaskDTO taskDTO) {
        try{
            Task task = taskService.createTakes(authentication,taskDTO);
            return ResponseEntity.ok().body(new TaskDTO(task));
        }
        catch (Exception e){
            return ResponseEntity.status(500).body(new MessageDTO(e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех задач")
    @ApiResponse(responseCode = "200", description = "Задача успешно добавлена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    @GetMapping("/all")
    public ResponseEntity<?> getAllTasksByToken(Authentication authentication) {
       try {
           List<Task> tasks= taskService.getAllTasks(authentication);
           return ResponseEntity.ok().body(tasks);
       } catch (Exception ex){
           return ResponseEntity.status(500).body(new MessageDTO(ex.getMessage()));
       }




    }
}

