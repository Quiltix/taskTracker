package com.quiltix.tasktracker.controller;


import com.quiltix.tasktracker.DTO.Category.CategoryDTO;
import com.quiltix.tasktracker.DTO.Category.ChangeCategoryDTO;
import com.quiltix.tasktracker.DTO.Category.CreateCategoryDTO;
import com.quiltix.tasktracker.DTO.Others.MessageDTO;
import com.quiltix.tasktracker.model.Category;
import com.quiltix.tasktracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping()
    public ResponseEntity<?> addCategory(Authentication authentication, @Valid @RequestBody CreateCategoryDTO categoryDTO){
        try {
            Category category = categoryService.addCategory(authentication,categoryDTO);
            return ResponseEntity.ok().body(new CategoryDTO(category));
        }catch (EntityExistsException ex){
            return ResponseEntity.badRequest().body(new MessageDTO(ex.getMessage()));
        } catch (Exception ex){
            return ResponseEntity.status(500).body(new MessageDTO(ex.getMessage()));
        }
    }

    @Operation(summary = "Удаление категории")
    @ApiResponse(responseCode = "200", description = "Успешное удаление")
    @ApiResponse(responseCode = "400", description = "Нет такой категории")
    @ApiResponse(responseCode = "401", description = "Нет прав на удаление")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")


    @DeleteMapping("/{id}")
    public ResponseEntity<?> addCategory(Authentication authentication, @PathVariable long id){
        try {
            categoryService.deleteCategory(authentication,id);
            return ResponseEntity.ok().body(new MessageDTO("Category deleted successfully"));
        } catch (Exception ex){
            return ResponseEntity.status(500).body(new MessageDTO(ex.getMessage()));
        }
    }

    @Operation(summary = "Изменение названия категории")
    @ApiResponse(responseCode = "200", description = "Успешное изменение")
    @ApiResponse(responseCode = "400", description = "Нет такой категории")
    @ApiResponse(responseCode = "401", description = "Нет прав на изменение")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")


    @PutMapping
    public ResponseEntity<?> changeCategory(Authentication authentication, @RequestBody @Valid ChangeCategoryDTO changeCategoryDTO){
        try {
            Category category = categoryService.changeCategoryName(authentication, changeCategoryDTO.getId(), changeCategoryDTO.getNewName());
            return ResponseEntity.ok().body(new CategoryDTO(category));
        } catch (Exception ex){
            return ResponseEntity.status(500).body(new MessageDTO(ex.getMessage()));
        }
    }
}
