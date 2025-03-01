package com.quiltix.tasktracker.controller;


import com.quiltix.tasktracker.DTO.Others.MessageDTO;
import com.quiltix.tasktracker.DTO.User.SetEmailDTO;
import com.quiltix.tasktracker.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Controller")
@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/email")
    public ResponseEntity<MessageDTO> setEmail(Authentication authentication, @Valid @RequestBody SetEmailDTO emailDTO){

        userService.updateEmail(authentication, emailDTO);

        return ResponseEntity.ok().body(new MessageDTO("Email updated successfully"));
    }


}
