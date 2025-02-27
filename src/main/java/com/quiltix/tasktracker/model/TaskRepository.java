package com.quiltix.tasktracker.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findByOwner(User owner);

    List<Task> findByOwnerAndCategory(User owner, Category category);

    List<Task> findByOwnerAndStatus(User owner, StatusEnum status);

    List<Task> findByOwnerAndImportant(User owner, boolean important);

    List<Task> findByStatusAndTimeToCompleteBefore(StatusEnum status, LocalDateTime now);


}
