package com.quiltix.tasktracker.DTO.Category;


import com.quiltix.tasktracker.model.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {

    private Long id;

    private String name;

    public CategoryDTO(Category category) {
        id = category.getId();
        name = category.getName();
    }
}
