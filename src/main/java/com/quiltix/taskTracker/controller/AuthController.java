package com.quiltix.taskTracker.controller;



import com.quiltix.taskTracker.DTO.JwtAuthenticationResponse;
import com.quiltix.taskTracker.DTO.LoginRequest;
import com.quiltix.taskTracker.DTO.RegisterRequest;
import com.quiltix.taskTracker.model.User;
import com.quiltix.taskTracker.model.UserRepository;
import com.quiltix.taskTracker.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
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

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthController(UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserRepository userRepository1, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository1;
        this.passwordEncoder = passwordEncoder;
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
                    schema = @Schema(type = "string",example = "Eror: User with this username is already exists")))

    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string",example = "Try again later")))
    @PostMapping("/register")
    public ResponseEntity <?> registerUser (@RequestBody RegisterRequest registerRequest){

        if (userRepository.findUserByUsername(registerRequest.getUsername()).isPresent()){
            return  ResponseEntity
                    .badRequest()
                    .body("Eror: User with this username is already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);

        return ResponseEntity
                .ok()
                .body("The user has been successfully registered");
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

    @ApiResponse(responseCode = "400", description = "Неверный логин или пароль",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string",example = "Invalid username or password")))

    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string",example = "Try again later")))
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            if( userRepository.findUserByUsername(loginRequest.getUsername()).isEmpty()){
                throw new UsernameNotFoundException("User not found");

            }
            // 1. Создаем объект аутентификации
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            // 2. Аутентифицируем пользователя
            Authentication authentication = authenticationManager.authenticate(authToken);

            // 3. Устанавливаем аутентификацию в SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 4. Генерируем JWT-токен
            String jwt = tokenProvider.generateToken(authentication);

            // 5. Отправляем успешный ответ
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

        } catch (UsernameNotFoundException ex) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid username or password");
        } catch (Exception ex) {
            return ResponseEntity
                    .internalServerError()
                    .body("Try again later");
        }
    }

}