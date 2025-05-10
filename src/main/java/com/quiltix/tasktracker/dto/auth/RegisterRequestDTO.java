
package com.quiltix.tasktracker.dto.auth;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RegisterRequestDTO {

    @Size(min = 3, message = "username must be at least 3 characters long")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Size(min = 7, message = "Password must be at least 7 characters long")
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
