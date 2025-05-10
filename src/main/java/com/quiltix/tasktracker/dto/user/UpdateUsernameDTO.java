package com.quiltix.tasktracker.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateUsernameDTO {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3,message = "Username must be at least 3 character")
    private String username;
}
