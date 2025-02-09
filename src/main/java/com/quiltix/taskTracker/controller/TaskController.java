package com.quiltix.taskTracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("api/tasks")
public class TaskController {



    // Получение задач текущего пользователя
    @GetMapping()
    public ResponseEntity<?> getAllTasks(Authentication authentication) {
        String username = authentication.getName();

        return ResponseEntity.ok("test "+ username);
    }
}

