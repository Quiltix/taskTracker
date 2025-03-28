package com.quiltix.tasktracker.controller;



import com.quiltix.tasktracker.DTO.Auth.JwtAuthenticationResponseDTO;
import com.quiltix.tasktracker.DTO.Auth.LoginRequestDTO;
import com.quiltix.tasktracker.DTO.Auth.ResetPasswordRequestDTO;
import com.quiltix.tasktracker.DTO.Others.MessageDTO;
import com.quiltix.tasktracker.DTO.Auth.RegisterRequestDTO;
import com.quiltix.tasktracker.DTO.User.ResetPasswordWithAuthDTO;
import com.quiltix.tasktracker.DTO.User.ResetPasswordWithCodeDTO;
import com.quiltix.tasktracker.service.RateLimiterService;
import com.quiltix.tasktracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "Auth Controller")
@RequestMapping("/api/auth")
public class AuthController {


    private final UserService userService;
    private final RateLimiterService rateLimiterService;

    public AuthController(UserService userService, RateLimiterService rateLimiterService) {

        this.rateLimiterService = rateLimiterService;
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
    public ResponseEntity <MessageDTO> registerUser ( @RequestBody @Valid RegisterRequestDTO registerRequestDTO){

        String message = userService.registerUser(registerRequestDTO);

        return ResponseEntity.ok().body(new MessageDTO(message));

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
    public ResponseEntity<JwtAuthenticationResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {


        String token = userService.authenticateUser(loginRequestDTO);

        return ResponseEntity.ok().body(new JwtAuthenticationResponseDTO(token));

    }


    @Operation(summary = "Отправка ресет кода по почте")
    @ApiResponse(responseCode = "200", description = "Успешное обновление")
    @ApiResponse(responseCode = "400", description = "Ошибка запроса")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    @PostMapping("/password")
    public ResponseEntity<MessageDTO> updatePassword( @Email @RequestBody ResetPasswordRequestDTO passwordRequestDTO ){

        if (!rateLimiterService.tryAcquire(passwordRequestDTO.getEmail())){
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new MessageDTO("Try again in 1 minute"));
        }


        userService.requestPasswordReset(passwordRequestDTO.getEmail());

        return ResponseEntity.ok().body( new MessageDTO("Check your email"));
    }

    @Operation(summary = "Изменение пароля с имеющимся ресет кодом")
    @ApiResponse(responseCode = "200", description = "Успешное обновление")
    @ApiResponse(responseCode = "400", description = "Ошибка запроса")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    @PutMapping("/password")
    public ResponseEntity<MessageDTO> updatePasswordWithResetCode(@Valid @RequestBody ResetPasswordWithCodeDTO dataDTO){

        userService.resetPasswordWithCode(dataDTO);

        return ResponseEntity.ok().body( new MessageDTO("Password updated successfully"));
    }

}