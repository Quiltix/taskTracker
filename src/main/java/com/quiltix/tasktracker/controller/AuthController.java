package com.quiltix.tasktracker.controller;



import com.quiltix.tasktracker.DTO.Auth.JwtAuthenticationResponseDTO;
import com.quiltix.tasktracker.DTO.Auth.LoginRequestDTO;
import com.quiltix.tasktracker.DTO.Others.MessageDTO;
import com.quiltix.tasktracker.DTO.Auth.RegisterRequestDTO;
import com.quiltix.tasktracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "Auth Controller")
@RequestMapping("/api/auth")
public class AuthController {


    private final UserService userService;


    public AuthController(UserService userService) {

        this.userService = userService;
    }

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Получает RegisterRequest"
    )
    @ApiResponse(
            responseCode = "200",description = "Успешная регистрация",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "string",example = "The user has been successfully registered"))
    )

    @ApiResponse(responseCode = "400", description = "Пользователь с таким именем уже существует",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string",example = "User with this username is already exists | Username/Password cannot be empty ")))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string",example = "Try again later")))
    @PostMapping("/register")
    public ResponseEntity <?> registerUser (@Valid @RequestBody RegisterRequestDTO registerRequestDTO){
        try {
            String message = userService.registerUser(registerRequestDTO);
            return ResponseEntity.ok().body(new MessageDTO(message));
        }
        catch (Exception ex){
            return ResponseEntity.status(500).body(new MessageDTO(ex.getMessage()));
        }
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Позволяет пользователю войти в систему и получить JWT токен"
    )
    @ApiResponse(
            responseCode = "200",description = "Успешная Аутентификация",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JwtAuthenticationResponseDTO.class)))

    @ApiResponse(responseCode = "400", description = "Логин или пароль пустой",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string",example = "Username/Password cannot be empty")))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string",example = "Try again later")))
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {

        try {
            String token = userService.authenticateUser(loginRequestDTO);
            return ResponseEntity.ok().body(new JwtAuthenticationResponseDTO(token));
        }catch (Exception ex){
            return ResponseEntity.status(500).body(new MessageDTO(ex.getMessage()));
        }

    }

}