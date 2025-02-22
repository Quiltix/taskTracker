package com.quiltix.tasktracker.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    private Boolean important;

    private LocalDateTime timeToComplete;

    private LocalDateTime startTime;

    @ManyToOne
    @JoinColumn(name = "Category_id")
    private Category category;

    @ManyToOne()
    @JoinColumn(name = "User_id")
    private User owner;

    private Boolean complete;

}
