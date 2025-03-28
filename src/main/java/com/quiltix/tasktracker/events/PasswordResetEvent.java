package com.quiltix.tasktracker.events;



import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class PasswordResetEvent {

    private String email;

    private String resetCode;

    private Long expirationEpochSec;


}
