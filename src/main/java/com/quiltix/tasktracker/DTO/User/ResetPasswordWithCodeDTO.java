package com.quiltix.tasktracker.DTO.User;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordWithCodeDTO {


    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Reset code cannot be empty")
    @Size(min = 6, message = "Reset code must be 6 characters long")
    private String resetCode;

    @Size(min = 7, message = "Password must be at least 7 characters long")
    private String newPassword;
}
