package com.quiltix.taskTracker.controller;


import com.quiltix.taskTracker.model.UserRepository;
import com.quiltix.taskTracker.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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


    @PostMapping
    public ResponseEntity <?> registerUser (@RequestBody LoginRe)
}
