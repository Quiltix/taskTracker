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
@Builder

public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    private Boolean important;
    /*
    Реализовать добавление таски
    Категории
    Реализовать решение таски
    И удаление

     */
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
