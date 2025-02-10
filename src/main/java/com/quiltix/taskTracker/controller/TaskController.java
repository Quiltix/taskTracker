package com.quiltix.taskTracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("api/tasks")
public class TaskController {



    // Получение задач текущего пользователя
    @PostMapping("/add")
    public ResponseEntity<?> getAllTasks(Authentication authentication) {
        String username = authentication.getName();


        return ResponseEntity.ok("test "+ username);
    }
}

