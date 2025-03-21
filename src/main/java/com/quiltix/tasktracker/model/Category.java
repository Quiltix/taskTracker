package com.quiltix.tasktracker.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 3, message = "Name must be at least 3 characters long")
    private String name;

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "User_id")
    private User owner;

}