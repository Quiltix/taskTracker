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

@Table(
        indexes = {
                @Index( name = "idx_owner", columnList = "User_id"),
                @Index(name = "idx_category", columnList = "category_id"),
        }
)
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

    @Enumerated(EnumType.ORDINAL)
    private StatusEnum status;

}
