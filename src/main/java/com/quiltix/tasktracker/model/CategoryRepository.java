package com.quiltix.tasktracker.model;


import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long>  {

    boolean existsByNameAndOwner(String name, User owner);
}
