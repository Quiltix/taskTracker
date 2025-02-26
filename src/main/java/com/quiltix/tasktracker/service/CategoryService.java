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

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository,UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
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


    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Authentication authentication,Long categoryId){

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category not found"));

        String username = authentication.getName();

        if (category.getOwner()== null || ! category.getOwner().getUsername().equals(username)){
            throw  new AccessDeniedException("You are not authorized to delete this category");
        }

        categoryRepository.delete(category);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public Category changeCategoryName(Authentication authentication, Long categoryId, String newName){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category not found"));

        String username = authentication.getName();

        if (category.getOwner()== null || ! category.getOwner().getUsername().equals(username)){
            throw  new AccessDeniedException("You are not authorized to change this category");
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
