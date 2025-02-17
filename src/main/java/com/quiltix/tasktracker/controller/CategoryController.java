package com.quiltix.tasktracker.controller;


import com.quiltix.tasktracker.DTO.CategoryDTO;
import com.quiltix.tasktracker.DTO.CreateCategoryDTO;
import com.quiltix.tasktracker.DTO.MessageDTO;
import com.quiltix.tasktracker.model.Category;
import com.quiltix.tasktracker.model.User;
import com.quiltix.tasktracker.model.UserRepository;
import com.quiltix.tasktracker.service.CategoryService;
import com.quiltix.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/task/category")
public class CategoryController {

    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(Authentication authentication, @Valid @RequestBody CreateCategoryDTO categoryDTO){
        try {
            Category category = categoryService.addCategory(authentication,categoryDTO);
            return ResponseEntity.ok().body(new CategoryDTO(category));
        } catch (Exception ex){
            return ResponseEntity.status(500).body(new MessageDTO(ex.getMessage()));

        }
    }
}
