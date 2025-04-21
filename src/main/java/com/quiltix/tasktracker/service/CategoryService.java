package com.quiltix.tasktracker.service;


import com.quiltix.tasktracker.DTO.Category.CategoryDTO;
import com.quiltix.tasktracker.DTO.Category.CreateCategoryDTO;
import com.quiltix.tasktracker.model.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @CacheEvict(value = "categories", allEntries = true)
    public Category addCategory(Authentication authentication, CreateCategoryDTO categoryDTO){
        User user = userRepository.findUserByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (categoryRepository.existsByNameAndOwner(categoryDTO.getName(),user)){
            throw new EntityExistsException("Category is already exists");
        }
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .owner(user)
                .build();
        return categoryRepository.save(category);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Authentication authentication,Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category not found"));

        String username = authentication.getName();

        if (category.getOwner()== null || ! category.getOwner().getUsername().equals(username)){
            throw  new AccessDeniedException("You are not authorized to delete this categoryId");
        }
        taskRepository.detachAllTasksFromCategory(category);

        categoryRepository.delete(category);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public Category changeCategoryName(Authentication authentication, Long categoryId, String newName){

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category not found"));

        String username = authentication.getName();

        if (category.getOwner()== null || ! category.getOwner().getUsername().equals(username)){
            throw  new AccessDeniedException("You are not authorized to change this categoryId");
        }
        category.setName(newName);

        return categoryRepository.save(category);
    }

    @Cacheable(value = "categories")
    public List<CategoryDTO> getAllCategories(Authentication authentication){

        String username = authentication.getName();

        User user = userRepository.findUserByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return categoryRepository.findByOwner(user).stream().map(CategoryDTO::new).toList();
    }
}
