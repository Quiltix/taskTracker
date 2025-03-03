package com.quiltix.tasktracker.DTO.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResetPasswordWithAuthDTO {

    @NotBlank(message = "old password cannot be empty")
    private String oldPassword;

    @Size(min = 5, message = "new password must be at least 5 characters long")
    @NotBlank(message =  "new password cannot be empty")
    private String newPassword;
}
