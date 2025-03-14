package com.quiltix.tasktracker.service;

import com.quiltix.tasktracker.DTO.Auth.LoginRequestDTO;
import com.quiltix.tasktracker.DTO.Auth.RegisterRequestDTO;
import com.quiltix.tasktracker.DTO.User.ResetPasswordWithAuthDTO;
import com.quiltix.tasktracker.DTO.User.ResetPasswordWithCodeDTO;
import com.quiltix.tasktracker.DTO.User.SetEmailDTO;
import com.quiltix.tasktracker.model.User;
import com.quiltix.tasktracker.model.UserRepository;
import com.quiltix.tasktracker.security.JwtTokenProvider;
import jakarta.persistence.EntityExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final Random random = new Random();
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

    public void updateEmail(Authentication authentication, SetEmailDTO emailDTO){
        String username = authentication.getName();

        User user = userRepository.findUserByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        userRepository.findByEmail(emailDTO.getEmail()).ifPresent(user1 -> {
            if (!user1.getEmail().equals(username)){
                throw new EntityExistsException("Email is already in use by another user");
            }
        });
        user.setEmail(emailDTO.getEmail());

        userRepository.save(user);
    }

    @Transactional
    public void updatePasswordWithAuth(Authentication authentication, ResetPasswordWithAuthDTO resetPasswordWithAuthDTO){
        String username = authentication.getName();

        User user = userRepository.findUserByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(resetPasswordWithAuthDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password doesn't match");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordWithAuthDTO.getNewPassword()));

        userRepository.save(user);
    }

    @Transactional
    public void requestPasswordReset(String email) {

        User user  = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User with email not found"));

        String resetCode = String.format("%06d", random.nextInt(6));
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(10);

        user.setResetCode(resetCode);
        user.setExpireCodeTime(expireTime);

        userRepository.save(user);
        System.out.println(resetCode);

    }

    @Transactional
    public void resetPasswordWithCode(ResetPasswordWithCodeDTO dataDto){

        User user = userRepository.findByEmail(dataDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User with email not found"));

        if (! (user.getResetCode().equals(dataDto.getResetCode()) && LocalDateTime.now().isAfter(user.getExpireCodeTime()))){
            throw new BadCredentialsException("Reset code incorrect, try again");
        }
        if (user.getResetCode() == null || user.getExpireCodeTime() == null) {
            throw new BadCredentialsException("Invalid or expired reset code");
        }

        user.setPassword(passwordEncoder.encode(dataDto.getNewPassword()));
        user.setResetCode(null);
        user.setExpireCodeTime(null);

        userRepository.save(user);


    }
}