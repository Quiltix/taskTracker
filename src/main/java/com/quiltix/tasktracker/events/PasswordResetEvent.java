package com.quiltix.tasktracker.events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetEvent {

    private String email;

    private String resetCode;

    private Long expirationEpochSec;


}
