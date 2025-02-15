package com.quiltix.taskTracker.config;


import com.quiltix.taskTracker.DTO.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex){
        return ResponseEntity.badRequest().body(new MessageDTO(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleValidationUserException(MethodArgumentNotValidException ex){
        return ResponseEntity.badRequest().body(new MessageDTO("User not found"));
    }
}
