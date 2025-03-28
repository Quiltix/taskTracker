package com.quiltix.tasktracker.controller;


import com.quiltix.tasktracker.DTO.Others.MessageDTO;
import com.quiltix.tasktracker.DTO.User.*;
import com.quiltix.tasktracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Controller")
@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Обновление почты")
    @ApiResponse(responseCode = "200", description = "Успешное обновление")
    @ApiResponse(responseCode = "400", description = "Ошибка запроса")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")

    @PutMapping("/email")
    public ResponseEntity<MessageDTO> setEmail(Authentication authentication, @Valid @RequestBody SetEmailDTO emailDTO){

        userService.updateEmail(authentication, emailDTO);

        return ResponseEntity.ok().body(new MessageDTO("Email updated successfully"));
    }

    @Operation(summary = "Изменение пароля с токеном (при нахождении авторизированным)")
    @ApiResponse(responseCode = "200", description = "Успешное обновление")
    @ApiResponse(responseCode = "400", description = "Ошибка запроса")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    @PutMapping("/password")
    public ResponseEntity<MessageDTO> updatePassword(Authentication authentication, @Valid @RequestBody ResetPasswordWithAuthDTO resetPasswordWithAuthDTO){

        userService.updatePasswordWithAuth(authentication, resetPasswordWithAuthDTO);

        return ResponseEntity.ok().body( new MessageDTO("Password updated successfully"));
    }

    @Operation(summary = "Изменение логина")
    @ApiResponse(responseCode = "200", description = "Успешное обновление")
    @ApiResponse(responseCode = "400", description = "Ошибка запроса")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    @PutMapping("/username")
    public ResponseEntity<UpdateUsernameDTO> updatePassword(Authentication authentication, @Valid @RequestBody UpdateUsernameDTO usernameDTO){

        String newName = userService.updateUsername(authentication, usernameDTO);

        return ResponseEntity.ok().body( new UpdateUsernameDTO(newName));
    }

    @Operation(summary = "Получение информации о пользователе")
    @ApiResponse(responseCode = "200", description = "Успешное получение")
    @ApiResponse(responseCode = "400", description = "Ошибка запроса")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    @GetMapping("/info")
    public ResponseEntity<ProfileInfoDTO> updatePassword(Authentication authentication){

         ProfileInfoDTO profileInfoDTO = userService.getProfileInfo(authentication);

        return ResponseEntity.ok().body(profileInfoDTO);
    }



}
