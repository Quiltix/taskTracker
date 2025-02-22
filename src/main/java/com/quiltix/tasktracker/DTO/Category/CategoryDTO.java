package com.quiltix.tasktracker.DTO.Category;


import com.quiltix.tasktracker.model.Category;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {

    private Long id;

    @Size(min = 3, message = "name must be at least 3 characters long")
    private String name;

    public CategoryDTO(Category category) {
        id = category.getId();
        name = category.getName();
    }
}
