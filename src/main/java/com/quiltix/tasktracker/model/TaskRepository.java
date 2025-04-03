package com.quiltix.tasktracker.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {

    @Modifying
    @Query("UPDATE Task t SET t.category = null WHERE t.category = :category")
    void detachAllTasksFromCategory(@Param("category") Category category);

    List<Task> findByOwner(User owner);

    List<Task> findByOwnerAndCategory(User owner, Category category);

    List<Task> findByOwnerAndStatus(User owner, StatusEnum status);

    List<Task> findByOwnerAndImportant(User owner, boolean important);

    List<Task> findByStatusAndTimeToCompleteBefore(StatusEnum status, LocalDateTime now);


}
