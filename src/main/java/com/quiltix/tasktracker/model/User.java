package com.quiltix.tasktracker.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 3, message = "Username must be at least 3 characters long")
    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Size(min = 7, message = "Password must be at least 7 characters long")
    @Column(nullable = false)
    private String password;
}
