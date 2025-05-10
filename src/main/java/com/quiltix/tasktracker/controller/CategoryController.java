package com.quiltix.tasktracker.controller;


import com.quiltix.tasktracker.dto.category.CategoryDTO;
import com.quiltix.tasktracker.dto.category.ChangeCategoryDTO;
import com.quiltix.tasktracker.dto.category.CreateCategoryDTO;
import com.quiltix.tasktracker.dto.others.MessageDTO;
import com.quiltix.tasktracker.model.Category;
import com.quiltix.tasktracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Category Controller")
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

    @PostMapping()
    public ResponseEntity<CategoryDTO> addCategory(Authentication authentication, @Valid @RequestBody CreateCategoryDTO categoryDTO){

        Category category = categoryService.addCategory(authentication,categoryDTO);

        return ResponseEntity.ok().body(new CategoryDTO(category));

    }

    @Operation(summary = "Удаление категории")
    @ApiResponse(responseCode = "200", description = "Успешное удаление")
    @ApiResponse(responseCode = "400", description = "Нет такой категории")
    @ApiResponse(responseCode = "401", description = "Нет прав на удаление")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")


    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> addCategory(Authentication authentication, @PathVariable long id){

        categoryService.deleteCategory(authentication,id);

        return ResponseEntity.ok().body(new MessageDTO("Category deleted successfully"));

    }

    @Operation(summary = "Изменение названия категории")
    @ApiResponse(responseCode = "200", description = "Успешное изменение")
    @ApiResponse(responseCode = "400", description = "Нет такой категории")
    @ApiResponse(responseCode = "401", description = "Нет прав на изменение")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")


    @PutMapping
    public ResponseEntity<CategoryDTO> changeCategory(Authentication authentication, @RequestBody @Valid ChangeCategoryDTO changeCategoryDTO){

        Category category = categoryService.changeCategoryName(authentication, changeCategoryDTO.getId(), changeCategoryDTO.getNewName());

        return ResponseEntity.ok().body(new CategoryDTO(category));

    }

    @Operation(summary = "Получение всех категорий")
    @ApiResponse(responseCode = "200", description = "Список категорий")
    @ApiResponse(responseCode = "400", description = "Нет такого пользователя")
    @ApiResponse(responseCode = "401", description = "Нет прав на чтение")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(Authentication authentication){

        List<CategoryDTO> categories = categoryService.getAllCategories(authentication);

        return ResponseEntity.ok().body(categories);

    }


}
