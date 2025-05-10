package com.quiltix.tasktracker.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Setter
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column( unique = true)
    private String email;

    private String resetCode;

    private LocalDateTime expireCodeTime;

    private String avatarUrl;
}
