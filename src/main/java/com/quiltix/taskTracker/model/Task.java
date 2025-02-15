package com.quiltix.taskTracker.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    private LocalDateTime timeToComplete;

    private LocalDateTime startTime;

    @ManyToOne
    @JoinColumn(name = "Category_id")
    private Category category;

    @ManyToOne()
    @JoinColumn(name = "User_id")
    private User owner;

    private boolean isComplete;

}
