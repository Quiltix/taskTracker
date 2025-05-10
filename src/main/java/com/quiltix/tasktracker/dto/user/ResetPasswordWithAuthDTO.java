package com.quiltix.tasktracker.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResetPasswordWithAuthDTO {

    @NotBlank(message = "old password cannot be empty")
    private String oldPassword;

    @Size(min = 7, message = "Password must be at least 7 characters long")
    @NotBlank(message =  "new password cannot be empty")
    private String newPassword;
}
