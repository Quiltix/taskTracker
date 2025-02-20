package com.quiltix.tasktracker.controller;


import com.quiltix.tasktracker.DTO.Category.CategoryDTO;
import com.quiltix.tasktracker.DTO.Category.CreateCategoryDTO;
import com.quiltix.tasktracker.DTO.Others.MessageDTO;
import com.quiltix.tasktracker.model.Category;
import com.quiltix.tasktracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @Operation(summary = "Добавление категории")
    @ApiResponse(responseCode = "200", description = "Категория успешно добавлена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
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
