package com.quiltix.tasktracker.service;

import com.quiltix.tasktracker.DTO.Auth.LoginRequestDTO;
import com.quiltix.tasktracker.DTO.Auth.RegisterRequestDTO;
import com.quiltix.tasktracker.model.User;
import com.quiltix.tasktracker.model.UserRepository;
import com.quiltix.tasktracker.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public String registerUser(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.findUserByUsername(registerRequestDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with this username already exists");
        }
        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        userRepository.save(user);

        return "The user has been successfully registered";

    }

    public String authenticateUser(LoginRequestDTO loginRequestDTO) {
        if(userRepository.findUserByUsername(loginRequestDTO.getUsername()).isEmpty()){
            throw new UsernameNotFoundException("User not found");
        }
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }
}