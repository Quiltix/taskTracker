package com.quiltix.tasktracker.dto.user;


import lombok.Data;

@Data
public class ProfileInfoDTO {

    private String username;

    private String email;

    private String avatarUrl;
}
