package com.quiltix.tasktracker.service;


import com.quiltix.tasktracker.DTO.Category.CreateCategoryDTO;
import com.quiltix.tasktracker.model.Category;
import com.quiltix.tasktracker.model.CategoryRepository;
import com.quiltix.tasktracker.model.User;
import com.quiltix.tasktracker.model.UserRepository;
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
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .owner(user)
                .build();
        return categoryRepository.save(category);
    }
}
