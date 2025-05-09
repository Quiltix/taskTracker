package com.quiltix.tasktracker.controller;

import com.quiltix.tasktracker.dto.task.CreateTaskDTO;
import com.quiltix.tasktracker.dto.others.MessageDTO;
import com.quiltix.tasktracker.dto.task.EditTaskDTO;
import com.quiltix.tasktracker.dto.task.TaskDTO;
import com.quiltix.tasktracker.model.StatusEnum;
import com.quiltix.tasktracker.model.Task;
import com.quiltix.tasktracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@Tag(name = "Task Controller")
@RequestMapping("api/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;

    }

    @Operation(summary = "Добавление новой задачи")
    @ApiResponse(responseCode = "200", description = "Задача успешно добавлена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @PostMapping
    public ResponseEntity<TaskDTO> addTask(Authentication authentication,@Valid @RequestBody CreateTaskDTO taskDTO){

        Task task = taskService.createTasks(authentication,taskDTO);

        return ResponseEntity.ok().body(new TaskDTO(task));
    }

    @Operation(summary = "Получение всех задач")
    @ApiResponse(responseCode = "200", description = "Задачи получены")
    @ApiResponse(responseCode = "400", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Server error")

    @GetMapping("/all")
    public ResponseEntity<List<TaskDTO>> getAllTasksByToken(Authentication authentication) {

        List<TaskDTO> tasks= taskService.getAllTasks(authentication);

        return ResponseEntity.ok().body(tasks);

    }

    @Operation(summary = "Удаление задачи")
    @ApiResponse(responseCode = "200", description = "Задача удалена")
    @ApiResponse(responseCode = "400", description = "Нет задачи с таким id")
    @ApiResponse(responseCode = "401", description = "Нет прав на удаление")
    @ApiResponse(responseCode = "500", description = "Server error")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<MessageDTO> deleteTask(Authentication authentication,@Valid @PathVariable long taskId) {

        taskService.deleteTask(authentication,taskId);

        return ResponseEntity.ok().body(new MessageDTO("Task deleted successfully"));

    }

    @Operation(summary = "Решение таски")
    @ApiResponse(responseCode = "200", description = "Задача решена")
    @ApiResponse(responseCode = "400", description = "Нет задачи с таким id")
    @ApiResponse(responseCode = "401", description = "Нет прав на изменение")
    @ApiResponse(responseCode = "500", description = "Server error")

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<TaskDTO> completeTask(Authentication authentication,@Valid @PathVariable long taskId) {

        Task task =  taskService.markTaskAsComplete(taskId,authentication);

        return ResponseEntity.ok().body(new TaskDTO(task));

    }

    @Operation(summary = "Изменение таски")
    @ApiResponse(responseCode = "200", description = "Задача изменена")
    @ApiResponse(responseCode = "400", description = "Нет задачи с таким id")
    @ApiResponse(responseCode = "401", description = "Нет прав на изменение")
    @ApiResponse(responseCode = "500", description = "Server error")

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> editTask(Authentication authentication, @PathVariable long id, @Valid @RequestBody EditTaskDTO editTaskDTO) {

        Task task =  taskService.editTask(authentication,id,editTaskDTO);

        return ResponseEntity.ok().body(new TaskDTO(task));

    }

    @Operation(summary = "Получение task по категории ")
    @ApiResponse(responseCode = "200", description = "Задачи получены")
    @ApiResponse(responseCode = "400", description = "Нет такой категории")
    @ApiResponse(responseCode = "401", description = "Нет прав на изменение")
    @ApiResponse(responseCode = "500", description = "Server error")
    @GetMapping("/by-categoryId")
    public ResponseEntity<List<TaskDTO>> filterByCategory(Authentication authentication, @RequestParam long id) {

        List<TaskDTO> tasks =  taskService.getTaskByCategory(authentication,id);

        return ResponseEntity.ok().body(tasks);

    }

    @Operation(summary = "Получение task по статусу ")
    @ApiResponse(responseCode = "200", description = "Задачи получены")
    @ApiResponse(responseCode = "400", description = "Нет такого статуса")
    @ApiResponse(responseCode = "401", description = "Нет прав на изменение")
    @ApiResponse(responseCode = "500", description = "Server error")
    @GetMapping("/by-status")
    public ResponseEntity<List<TaskDTO>> filterByStatus(Authentication authentication, @RequestParam StatusEnum status) {

        List<TaskDTO> tasks =  taskService.getTaskByStatus(authentication,status);

        return ResponseEntity.ok().body(tasks);

    }


    @Operation(summary = "Получение важных task ")
    @ApiResponse(responseCode = "200", description = "Задачи получены")
    @ApiResponse(responseCode = "401", description = "Нет прав на получение")
    @ApiResponse(responseCode = "500", description = "Server error")
    @GetMapping("/important")
    public ResponseEntity<List<TaskDTO>> getImportantTasks(Authentication authentication) {
        List<TaskDTO> tasks = taskService.getImportantTasks(authentication);
        return ResponseEntity.ok(tasks);
    }

}

