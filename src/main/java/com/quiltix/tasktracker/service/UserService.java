package com.quiltix.tasktracker.service;

import com.quiltix.tasktracker.DTO.Auth.LoginRequestDTO;
import com.quiltix.tasktracker.DTO.Auth.RegisterRequestDTO;
import com.quiltix.tasktracker.DTO.User.*;
import com.quiltix.tasktracker.events.PasswordResetEvent;
import com.quiltix.tasktracker.model.User;
import com.quiltix.tasktracker.model.UserRepository;
import com.quiltix.tasktracker.security.JwtTokenProvider;
import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;


@Service
@Slf4j
public class UserService {

    @Value("${app.avatar-dir}")
    private String avatarDir;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final KafkaTemplate<String, PasswordResetEvent> kafkaTemplate;
    private final Random random = new Random();
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, KafkaTemplate<String, PasswordResetEvent> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.kafkaTemplate = kafkaTemplate;
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

        User user = getCurrentUser(authentication);

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

        User user = getCurrentUser(authentication);

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

        PasswordResetEvent event = PasswordResetEvent.builder()
                .email(email)
                .resetCode(resetCode)
                .expirationEpochSec(expireTime.toEpochSecond(ZoneOffset.UTC))
                .build();

        kafkaTemplate.send("password-reset",event);
    }

    @Transactional
    public void resetPasswordWithCode(ResetPasswordWithCodeDTO dataDto){

        User user = userRepository.findByEmail(dataDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User with email not found"));

        if (! (user.getResetCode().equals(dataDto.getResetCode()) && LocalDateTime.now().isBefore(user.getExpireCodeTime()))){
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

    @Transactional
    public String updateUsername(Authentication authentication, UpdateUsernameDTO usernameDTO){

        String oldUsername = authentication.getName();

        if (usernameDTO.getUsername().equals(oldUsername)){
            throw new IllegalArgumentException("the username must not match");
        }

        if(userRepository.findUserByUsername(usernameDTO.getUsername()).isPresent()){
            throw new BadCredentialsException("This username already defined");
        }

        User user = getCurrentUser(authentication);

        user.setUsername(usernameDTO.getUsername());

        userRepository.save(user);

        return user.getUsername();
    }

    public ProfileInfoDTO getProfileInfo(Authentication authentication){

        String username = authentication.getName();

        User user = getCurrentUser(authentication);

        ProfileInfoDTO profileInfoDTO = new ProfileInfoDTO();

        profileInfoDTO.setUsername(username);
        profileInfoDTO.setEmail(user.getEmail());
        profileInfoDTO.setAvatarUrl(user.getAvatarUrl() != null ? "/avatars/" + user.getAvatarUrl() : null);

        return profileInfoDTO;
    }

    public void updateAvatar(Authentication authentication, MultipartFile file){

        User user = getCurrentUser(authentication);

        if(file.isEmpty()){
            throw new IllegalArgumentException("File is empty");
        }
        try {
            String mimeType = new Tika().detect(file.getBytes());
            if (!mimeType.startsWith("image/")) {
                throw new IllegalArgumentException("Invalid image format");
            }
        } catch (IOException e) {
            log.error("Error reading file", e);
            throw new RuntimeException("Error reading file", e);
        }

        String fileName = UUID.randomUUID()+ file.getOriginalFilename()
                                            .substring(file.getOriginalFilename()
                                            .lastIndexOf("."));

        Path targetPath = Path.of(avatarDir,fileName);

        String oldFileName = user.getAvatarUrl();
        saveFile(file,targetPath);


        user.setAvatarUrl(fileName);
        userRepository.save(user);

        deleteOldAvatar(oldFileName);


    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findUserByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private void saveFile(MultipartFile file, Path targetPath){
        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    private void deleteOldAvatar(String oldAvatarPath) {
        if (oldAvatarPath != null) {
            try {
                Path path = Paths.get(avatarDir, oldAvatarPath);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to remove file", e);
            }
        }
    }
}