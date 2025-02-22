package com.quiltix.tasktracker.config;


import com.quiltix.tasktracker.DTO.Others.MessageDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex){
        System.out.println(1);
        return ResponseEntity.badRequest().body(new MessageDTO(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundExceptions(UsernameNotFoundException ex){
        return ResponseEntity.badRequest().body(new MessageDTO("User not found"));
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedExceptions(AccessDeniedException ex){
        return ResponseEntity.status(401).body(new MessageDTO(ex.getMessage()));
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundExceptions(EntityNotFoundException ex){
        return ResponseEntity.status(400).body(new MessageDTO(ex.getMessage()));
    }
}
