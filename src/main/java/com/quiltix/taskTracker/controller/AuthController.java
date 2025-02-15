package com.quiltix.taskTracker.controller;



import com.quiltix.taskTracker.DTO.JwtAuthenticationResponse;
import com.quiltix.taskTracker.DTO.LoginRequest;
import com.quiltix.taskTracker.DTO.MessageDTO;
import com.quiltix.taskTracker.DTO.RegisterRequest;
import com.quiltix.taskTracker.model.User;
import com.quiltix.taskTracker.model.UserRepository;
import com.quiltix.taskTracker.security.JwtTokenProvider;
import com.quiltix.taskTracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "AuthController")
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
    public ResponseEntity <?> registerUser (@Valid @RequestBody RegisterRequest registerRequest){
        try {
            String message = userService.registerUser(registerRequest);
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
                    schema = @Schema(implementation = JwtAuthenticationResponse.class)))

    @ApiResponse(responseCode = "400", description = "Логин или пароль пустой",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string",example = "Username/Password cannot be empty")))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string",example = "Try again later")))
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            String token = userService.authenticateUser(loginRequest);
            return ResponseEntity.ok().body(new JwtAuthenticationResponse(token));
        }
        catch (UsernameNotFoundException ex){
            return ResponseEntity.badRequest().body(new MessageDTO(ex.getMessage()));
        } catch (Exception ex){
            return ResponseEntity.status(500).body(new MessageDTO(ex.getMessage()));
        }

    }

}