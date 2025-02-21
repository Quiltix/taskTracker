package com.quiltix.tasktracker.service;


import com.quiltix.tasktracker.DTO.Category.CreateCategoryDTO;
import com.quiltix.tasktracker.model.Category;
import com.quiltix.tasktracker.model.CategoryRepository;
import com.quiltix.tasktracker.model.User;
import com.quiltix.tasktracker.model.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository,UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

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


    public void deleteCategory(Authentication authentication,Long categoryId){

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category not found"));

        String username = authentication.getName();

        if (category.getOwner()== null || ! category.getOwner().getUsername().equals(username)){
            throw  new AccessDeniedException("You are not authorized to delete this category");
        }

        categoryRepository.delete(category);
    }

    public Category changeCategoryName(Authentication authentication, Long categoryId, String newName){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category not found"));

        String username = authentication.getName();

        if (category.getOwner()== null || ! category.getOwner().getUsername().equals(username)){
            throw  new AccessDeniedException("You are not authorized to change this category");
        }
        category.setName(newName);

        return categoryRepository.save(category);

    }
}
