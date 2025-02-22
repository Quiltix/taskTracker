package com.quiltix.tasktracker.model;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long>  {

    boolean existsByNameAndOwner(String name, User owner);
    List<Category> findByOwner(User owner);
}
