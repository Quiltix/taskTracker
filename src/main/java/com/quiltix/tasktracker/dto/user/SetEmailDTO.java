package com.quiltix.tasktracker.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetEmailDTO {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;
}
