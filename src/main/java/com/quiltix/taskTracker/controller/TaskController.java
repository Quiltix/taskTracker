package com.quiltix.taskTracker.controller;

import com.quiltix.taskTracker.DTO.CreateTaskDTO;
import com.quiltix.taskTracker.DTO.MessageDTO;
import com.quiltix.taskTracker.model.Task;
import com.quiltix.taskTracker.model.User;
import com.quiltix.taskTracker.model.UserRepository;
import com.quiltix.taskTracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("api/tasks")
public class TaskController {


    private final TaskService taskService;

    private final UserRepository userRepository;

    public TaskController(TaskService taskService,
                          UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTask(Authentication authentication) {
        String username = authentication.getName();

        return ResponseEntity.ok().body("ad");
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTasksByToken(Authentication authentication) {
       try {
           List<Task> tasks= taskService.getAllTasks(authentication);
           return ResponseEntity.ok().body(tasks);
       } catch (UsernameNotFoundException ex){
           return ResponseEntity.badRequest().body(new MessageDTO(ex.getMessage()));
       } catch (Exception ex){
           return ResponseEntity.status(500).body(new MessageDTO(ex.getMessage()));
       }




    }
}

